package com.bxtz;

import com.opencsv.bean.CsvBindByPosition;
import javafx.beans.property.SimpleStringProperty;

public class Bill {

    @CsvBindByPosition(position = 0)
    private final SimpleStringProperty date;

    @CsvBindByPosition(position = 1)
    private final SimpleStringProperty name;

    @CsvBindByPosition(position = 2)
    private final SimpleStringProperty details;

    @CsvBindByPosition(position = 3)
    private final SimpleStringProperty cost;

    @CsvBindByPosition(position = 4)
    private final SimpleStringProperty  type;

    public Bill(String date, String name, String details, String cost, String type) {
        this.date = new SimpleStringProperty(date);
        this.name = new SimpleStringProperty(name);
        this.details = new SimpleStringProperty(details);
        this.cost = new SimpleStringProperty(cost);
        this.type = new SimpleStringProperty(type);
    }

    public Bill(String[] strings) {
        this.date = new SimpleStringProperty(strings[0]);
        this.name = new SimpleStringProperty(strings[1]);
        this.details = new SimpleStringProperty(strings[2]);
        this.cost = new SimpleStringProperty(strings[3]);
        this.type = new SimpleStringProperty(strings[4]);
    }

    public String getDate() {
        return date.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDetails() {
        return details.get();
    }

    public String getCost() {
        return cost.get();
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
    public SimpleStringProperty detailsProperty() {
        return details;
    }
    public SimpleStringProperty costProperty() {
        return cost;
    }
    public SimpleStringProperty typeProperty() {
        return type;
    }
}
