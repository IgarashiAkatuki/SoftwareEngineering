package com.bxtz.utils;

import com.bxtz.Bill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BillUtilsTest {

    @TempDir
    Path tempDir; // JUnit 5 temporary directory

    @Test
    void testExportAndImportCSV() throws IOException {
        List<Bill> billsToExport = Arrays.asList(
                new Bill("2025-01-01 10:00", "Lunch", "Restaurant", "50 RMB", "Food"),
                new Bill("2025-01-02 15:00", "Groceries", "Supermarket", "150 RMB", "Shopping")
        );

        File csvFile = tempDir.resolve("test_bills.csv").toFile();

        // Test export
        BillUtils.exportToCSV(billsToExport, csvFile); //
        assertTrue(csvFile.exists(), "CSV file should be created");
        assertTrue(csvFile.length() > 0, "CSV file should not be empty");

        // Verify some content (basic check)
        List<String> lines = Files.readAllLines(csvFile.toPath());
        // Expected: Header (implicitly written by StatefulBeanToCsv if not configured otherwise, or just data if it uses position)
        // Opencsv's StatefulBeanToCsv writes beans directly, so there's no automatic "NAME" header as in the import logic.
        // The provided importFromCSV skips a line if strings[0] is "NAME".
        // This implies the CSV might have a header. Let's assume the export doesn't write a header for this test,
        // or adjust if StatefulBeanToCsv default behavior implies headers.
        // For simplicity, let's check the number of data lines.
        // The provided exportToCSV doesn't explicitly write a header.
        // The provided importFromCSV has a specific check: if ("NAME".equals(strings[0])) { continue; }
        // This suggests that the CSV files it expects to import might have a header like "NAME,..."
        // However, the Bill class uses @CsvBindByPosition, which typically doesn't involve named headers for writing unless specified.
        // Let's assume for now `exportToCSV` writes data directly.

        assertEquals(2, lines.size(), "CSV file should contain 2 data lines (if no header from export)");


        // Test import
        List<Bill> importedBills = BillUtils.importFromCSV(csvFile); //
        assertNotNull(importedBills, "Imported bills should not be null");
        assertEquals(2, importedBills.size(), "Should import 2 bills");

        assertEquals("Lunch", importedBills.get(0).getName());
        assertEquals("50 RMB", importedBills.get(0).getCost());
        assertEquals("Food", importedBills.get(0).getType());

        assertEquals("Groceries", importedBills.get(1).getName());
        assertEquals("150 RMB", importedBills.get(1).getCost());
        assertEquals("Shopping", importedBills.get(1).getType());
    }

    @Test
    void testImportCSVWithHeader() throws IOException {
        File csvFileWithHeader = tempDir.resolve("header_bills.csv").toFile();
        List<String> csvContent = Arrays.asList(
                "NAME,DATE,DETAILS,COST,TYPE", // A header that matches the import logic's skip condition more broadly
                "\"Coffee\",\"2025-01-03 09:00\",\"Cafe\",\"25 RMB\",\"Food\""
        );
        Files.write(csvFileWithHeader.toPath(), csvContent);

        List<Bill> importedBills = BillUtils.importFromCSV(csvFileWithHeader);
        assertNotNull(importedBills);
        // The current import logic specifically checks `if ("NAME".equals(strings[0]))`.
        // If the first field of the header is "NAME", it will be skipped.
        // So, if the header is "NAME,DATE...", it will be skipped.
        // The data line provided "Coffee" is the first field.
        // The Bill constructor expects: date, name, details, cost, type.
        // The sample data line is: name, date, details, cost, type. This order is different from Bill constructor.
        // The Bill class uses @CsvBindByPosition for export.
        // The Bill(String[] strings) constructor uses strings[0] as date, strings[1] as name etc.
        // The CSV data for import must match this order.

        // Let's re-create the CSV content to match the Bill(String[] strings) constructor order
        // And ensure the header starts with "NAME" to test the skip.
        csvContent = Arrays.asList(
                "DATE,NAME,DETAILS,COST,TYPE", // This header will NOT be skipped by `if ("NAME".equals(strings[0]))`
                "\"2025-01-03 09:00\",\"Coffee\",\"Cafe\",\"25 RMB\",\"Food\""
        );
        Files.write(csvFileWithHeader.toPath(), csvContent);
        importedBills = BillUtils.importFromCSV(csvFileWithHeader);
        // Since the header's first element "DATE" is not "NAME", the header itself will be parsed as a bill, likely causing an error or an incorrect bill.
        // This shows a potential fragility in the header skipping logic.
        // To correctly test the skip, the header MUST start with "NAME".

        // Corrected CSV for testing the "NAME" header skip
        List<String> csvContentCorrected = Arrays.asList(
                "NAME,SOME_OTHER_DATE_COLUMN_FOR_HEADER_ONLY,SOME_DETAILS,SOME_COST,SOME_TYPE", // This header WILL be skipped.
                "\"2025-01-03 09:00\",\"Coffee\",\"Cafe\",\"25 RMB\",\"Food\"" // Data line
        );
        Files.write(csvFileWithHeader.toPath(), csvContentCorrected);
        importedBills = BillUtils.importFromCSV(csvFileWithHeader);

        assertEquals(1, importedBills.size(), "Should import 1 bill after skipping the 'NAME' header");
        assertEquals("2025-01-03 09:00", importedBills.get(0).getDate());
        assertEquals("Coffee", importedBills.get(0).getName());
    }

    @Test
    void testImportEmptyCSV() throws IOException {
        File emptyCsvFile = tempDir.resolve("empty.csv").toFile();
        emptyCsvFile.createNewFile();

        List<Bill> bills = BillUtils.importFromCSV(emptyCsvFile);
        assertTrue(bills.isEmpty(), "Importing an empty CSV should result in an empty list");
    }
}