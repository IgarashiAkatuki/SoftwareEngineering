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

        csvContent = Arrays.asList(
                "DATE,NAME,DETAILS,COST,TYPE", // This header will NOT be skipped by `if ("NAME".equals(strings[0]))`
                "\"2025-01-03 09:00\",\"Coffee\",\"Cafe\",\"25 RMB\",\"Food\""
        );
        Files.write(csvFileWithHeader.toPath(), csvContent);
        importedBills = BillUtils.importFromCSV(csvFileWithHeader);

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