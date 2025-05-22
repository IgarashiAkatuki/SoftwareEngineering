/**
 * Represents a bill/expense record with properties bound for CSV serialization.
 * This class serves as a model for financial transactions with properties that can be
 * easily displayed in JavaFX TableView and exported to/from CSV files.
 */
package com.bxtz;

import com.opencsv.bean.CsvBindByPosition;
import javafx.beans.property.SimpleStringProperty;

public class Bill {
    /**
     * The date of the bill in string format, bound to position 0 in CSV
     */
    @CsvBindByPosition(position = 0)
    private final SimpleStringProperty date;

    /**
     * The name/payee of the bill, bound to position 1 in CSV
     */
    @CsvBindByPosition(position = 1)
    private final SimpleStringProperty name;

    /**
     * Additional details about the bill, bound to position 2 in CSV
     */
    @CsvBindByPosition(position = 2)
    private final SimpleStringProperty details;

    /**
     * The cost amount of the bill, bound to position 3 in CSV
     */
    @CsvBindByPosition(position = 3)
    private final SimpleStringProperty cost;

    /**
     * The category/type of the bill, bound to position 4 in CSV
     */
    @CsvBindByPosition(position = 4)
    private final SimpleStringProperty type;

    /**
     * Constructs a new Bill with all properties specified.
     *
     * @param date The date of the bill (format: yyyy-MM-dd HH:mm)
     * @param name The name/payee of the bill
     * @param details Additional details about the bill
     * @param cost The amount of the bill (with "RMB" suffix)
     * @param type The category of the bill
     */
    public Bill(String date, String name, String details, String cost, String type) {
        this.date = new SimpleStringProperty(date);
        this.name = new SimpleStringProperty(name);
        this.details = new SimpleStringProperty(details);
        this.cost = new SimpleStringProperty(cost);
        this.type = new SimpleStringProperty(type);
    }

    /**
     * Constructs a new Bill from a string array (typically from CSV import).
     * Array positions must match CSV binding positions.
     *
     * @param strings String array containing bill properties in order:
     *                [date, name, details, cost, type]
     */
    public Bill(String[] strings) {
        this.date = new SimpleStringProperty(strings[0]);
        this.name = new SimpleStringProperty(strings[1]);
        this.details = new SimpleStringProperty(strings[2]);
        this.cost = new SimpleStringProperty(strings[3]);
        this.type = new SimpleStringProperty(strings[4]);
    }

    /**
     * @return The date of the bill as String
     */
    public String getDate() {
        return date.get();
    }

    /**
     * @return The name/payee of the bill as String
     */
    public String getName() {
        return name.get();
    }

    /**
     * @return The details of the bill as String
     */
    public String getDetails() {
        return details.get();
    }

    /**
     * @return The cost amount of the bill as String (with "RMB" suffix)
     */
    public String getCost() {
        return cost.get();
    }

    /**
     * @return The category/type of the bill as String
     */
    public String getType() {
        return type.get();
    }

    /**
     * @return The date property for JavaFX binding
     */
    public SimpleStringProperty dateProperty() {
        return date;
    }

    /**
     * @return The name property for JavaFX binding
     */
    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * @return The details property for JavaFX binding
     */
    public SimpleStringProperty detailsProperty() {
        return details;
    }

    /**
     * @return The cost property for JavaFX binding
     */
    public SimpleStringProperty costProperty() {
        return cost;
    }

    /**
     * @return The type property for JavaFX binding
     */
    public SimpleStringProperty typeProperty() {
        return type;
    }
}