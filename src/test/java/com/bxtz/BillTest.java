package com.bxtz;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    @Test
    void testBillConstructorAndGetters() {
        String date = "2025-05-23 10:00";
        String name = "Test Item";
        String details = "Test Details";
        String cost = "100 RMB";
        String type = "Test Type";

        Bill bill = new Bill(date, name, details, cost, type); //

        assertEquals(date, bill.getDate(), "Date should match");
        assertEquals(name, bill.getName(), "Name should match");
        assertEquals(details, bill.getDetails(), "Details should match");
        assertEquals(cost, bill.getCost(), "Cost should match");
        assertEquals(type, bill.getType(), "Type should match");

        assertNotNull(bill.dateProperty(), "Date property should not be null");
        assertNotNull(bill.nameProperty(), "Name property should not be null");
        assertNotNull(bill.detailsProperty(), "Details property should not be null");
        assertNotNull(bill.costProperty(), "Cost property should not be null");
        assertNotNull(bill.typeProperty(), "Type property should not be null");
    }

    @Test
    void testBillConstructorWithArray() {
        String[] billData = {"2025-05-24 11:00", "Array Item", "Array Details", "200 RMB", "Array Type"};
        Bill bill = new Bill(billData); //

        assertEquals(billData[0], bill.getDate());
        assertEquals(billData[1], bill.getName());
        assertEquals(billData[2], bill.getDetails());
        assertEquals(billData[3], bill.getCost());
        assertEquals(billData[4], bill.getType());
    }
}