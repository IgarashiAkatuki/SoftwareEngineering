package com.bxtz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonsTest {

    private final Commons commons = new Commons(); //

    @Mock
    private Label mockTotalCostLabel;

    @BeforeAll
    static void initJavaFX() {
        try {
            new javafx.embed.swing.JFXPanel(); // Initializes JavaFX toolkit
        } catch (Exception e) {
            System.err.println("Could not initialize JavaFX Toolkit for testing: " + e.getMessage());
        }
    }

    @Test
    void testUpdateTotalCost() {
        ObservableList<Bill> bills = FXCollections.observableArrayList(
                new Bill("d1", "n1", "de1", "10.50 RMB", "t1"),
                new Bill("d2", "n2", "de2", "20.00 RMB", "t2"),
                new Bill("d3", "n3", "de3", "", "t3"),
                new Bill("d4", "n4", "de4", "5.25 RMB", "t4")
        );

        commons.updateTotalCost(bills, mockTotalCostLabel); //

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockTotalCostLabel).setText(captor.capture());

        // 10.50 + 20.00 + 5.25 = 35.75
        assertEquals("Total Cost: 35.75 RMB", captor.getValue(), "Total cost calculation is incorrect.");
    }

    @Test
    void testUpdateTotalCost_emptyList() {
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        commons.updateTotalCost(bills, mockTotalCostLabel); //

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockTotalCostLabel).setText(captor.capture());
        // Corrected assertion:
        assertEquals("Total Cost: 0.00 RMB", captor.getValue());
    }

    @Test
    void testIsValidDate() {
        assertTrue(commons.isValidDate("2023-01-01 10:00"), "Valid date format should return true"); //
        assertFalse(commons.isValidDate("2023/01/01 10:00"), "Invalid date format should return false");
        assertFalse(commons.isValidDate("2023-01-01"), "Date without time should be false by current format");
        assertFalse(commons.isValidDate("not a date"), "Gibberish date should be false");
        assertFalse(commons.isValidDate("2023-13-01 10:00"), "Invalid month should be false");
        assertFalse(commons.isValidDate(null), "Null date should be false");
        assertFalse(commons.isValidDate(" "), "Empty date should be false");
    }

    @Test
    void testIsValidCost() {
        assertTrue(commons.isValidCost("100"), "Valid cost (number string) should return true"); //
        assertTrue(commons.isValidCost("123.45"), "Valid cost with decimal should return true");
        assertTrue(commons.isValidCost("0"), "Zero cost should be valid");
        assertFalse(commons.isValidCost("abc"), "Invalid cost (non-numeric) should return false");
        // The isValidCost method in Commons.java now only validates the numeric part.
        // The suffix " RMB" is handled separately when creating/editing bills.
        assertFalse(commons.isValidCost("100 RMB"), "Cost with ' RMB' should be false for this specific validator");
        assertFalse(commons.isValidCost(null), "Null cost should be false");
        assertFalse(commons.isValidCost(" "), "Empty cost should be false");
    }
}