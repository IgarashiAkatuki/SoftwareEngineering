package com.bxtz;

import com.bxtz.utils.BillUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commons {
    public void updateTotalCost(ObservableList<Bill> bills, Label totalCost) {
        double sum = 0.0;
        for (Bill b : bills) {
            if ("".equals(b.getCost())) {
                continue;
            }
            sum += Double.parseDouble(b.getCost().replace(" RMB", ""));
        }
        totalCost.setText("Total Cost: " + sum + " RMB");
    }

    public void styleButton1(Button btn) {
        btn.setStyle("-fx-background-color: #3c9cfc; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    public void styleButton2(Button btn) {
        btn.setStyle("-fx-background-color: #65c43d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    public void styleButton3(Button btn) {
        btn.setStyle("-fx-background-color: #e4a43c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    public void styleButton4(Button btn) {
        btn.setStyle("-fx-background-color: #F56C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    public void styleButton5(Button btn) {
        btn.setStyle("-fx-background-color: #909399; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    public void showEditDialog(Bill bill, TableView<Bill> table, Label totalCost) {
        Dialog<Bill> dialog = new Dialog<>();
        dialog.setTitle("Edit Bill");

        TextField dateField = new TextField(bill.getDate());
        TextField nameField = new TextField(bill.getName());
        TextField detailsField = new TextField(bill.getDetails());
        TextField costField = new TextField(bill.getCost());
        TextField typeField = new TextField(bill.getType());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Details:"), 0, 2);
        grid.add(detailsField, 1, 2);
        grid.add(new Label("Cost:"), 0, 3);
        grid.add(costField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeField, 1, 4);

        Button deleteBtn = new Button("Delete Bill");
        styleButton4(deleteBtn);
        deleteBtn.setOnAction(edited -> {
            ObservableList<Bill> bills = table.getItems();
            bills.remove(bill);
            table.setItems(bills);
            updateTotalCost(table.getItems(), totalCost);
            table.refresh();
        });
        grid.add(deleteBtn, 1, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Bill(
                        dateField.getText(),
                        nameField.getText(),
                        detailsField.getText(),
                        costField.getText(),
                        typeField.getText()
                );
            }
            return null;
        });

        Optional<Bill> result = dialog.showAndWait();

        result.ifPresent(edited -> {
            bill.dateProperty().set(edited.getDate());
            bill.nameProperty().set(edited.getName());
            bill.detailsProperty().set(edited.getDetails());
            bill.costProperty().set(edited.getCost());
            bill.typeProperty().set(edited.getType());
            updateTotalCost(table.getItems(), totalCost);
            table.refresh();
        });
    }

    public Button createExportButton(TableView<Bill> table, Label totalCost, Stage stage) {
        Button exportBtn = new Button("Download Bills");
        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("save CSV file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                BillUtils.exportToCSV(table.getItems(), file);
            }
        });
        return exportBtn;
    }

    public Button createImportButton(TableView<Bill> table, Label totalCost, Stage stage) {
        Button importBtn = new Button("Upload Bills");
        importBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("export CSV file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                List<Bill> newBills = BillUtils.importFromCSV(file);
                ObservableList<Bill> bills = FXCollections.observableArrayList(newBills);
                table.setItems(bills);
                updateTotalCost(bills, totalCost);
            }
        });
        return importBtn;
    }

    public void showAddDialog(TableView<Bill> table, Label totalCost) {
        Dialog<Bill> dialog = new Dialog<>();
        dialog.setTitle("Add Bill");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TextField dateField = new TextField(LocalDateTime.now().format(formatter));
        TextField nameField = new TextField("");
        TextField detailsField = new TextField("");
        TextField costField = new TextField("");

        // Create a ToggleGroup to ensure only one RadioButton can be selected at a time
        ToggleGroup typeGroup = new ToggleGroup();

        // Create RadioButtons for predefined types
        RadioButton foodButton = new RadioButton("Food");
        foodButton.setToggleGroup(typeGroup);
        RadioButton shoppingButton = new RadioButton("Shopping");
        shoppingButton.setToggleGroup(typeGroup);
        RadioButton entertainmentButton = new RadioButton("Entertainment");
        entertainmentButton.setToggleGroup(typeGroup);
        RadioButton othersButton = new RadioButton("Others");
        othersButton.setToggleGroup(typeGroup);

        // Make sure one option is selected by default
        foodButton.setSelected(true);

        // Style the buttons for consistency
        styleRadioButton(foodButton);
        styleRadioButton(shoppingButton);
        styleRadioButton(entertainmentButton);
        styleRadioButton(othersButton);

        VBox typeBox = new VBox(10, foodButton, shoppingButton, entertainmentButton, othersButton);
        typeBox.setSpacing(5);

        // Add consistent width to the labels and buttons
        setRadioButtonWidth(foodButton);
        setRadioButtonWidth(shoppingButton);
        setRadioButtonWidth(entertainmentButton);
        setRadioButtonWidth(othersButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Details:"), 0, 2);
        grid.add(detailsField, 1, 2);
        grid.add(new Label("Cost:"), 0, 3);
        grid.add(costField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String date = dateField.getText();
                String cost = costField.getText();

                // Validate the date format
                if (!isValidDate(date)) {
                    showErrorDialog("Format error: Invalid date format. Please use yyyy-MM-dd HH:mm.");
                    return null;  // Keep the dialog open
                    return null;
                }

                // Validate the cost format
                if (!isValidCost(cost)) {
                    showErrorDialog("Format error: Invalid cost format. Please enter a valid number followed by 'RMB'.");
                    return null;  // Keep the dialog open
                }

                // Add " RMB" to the cost if it's not present
                if (!cost.endsWith("RMB")) {
                    cost = cost + " RMB";
                }

                // Get the selected type from the radio buttons
                RadioButton selectedRadioButton = (RadioButton) typeGroup.getSelectedToggle();
                String type = selectedRadioButton.getText();  // Fix: Getting the text of the selected RadioButton

                // Create a new Bill using the fields
                return new Bill(
                        date,
                        nameField.getText(),
                        detailsField.getText(),
                        cost,
                        type  // This is now properly set to the selected type
                );
            }
            return null;
        });

        Optional<Bill> result = dialog.showAndWait();

        result.ifPresent(edited -> {
            ObservableList<Bill> bills = table.getItems();
            bills.add(edited);
            table.setItems(bills);
            updateTotalCost(table.getItems(), totalCost);
            table.refresh();
        });
    }

    // Method to style the RadioButton for consistency
    private void styleRadioButton(RadioButton radioButton) {
        radioButton.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-padding: 5;");
    }

    // Method to set a uniform width for the RadioButton
    private void setRadioButtonWidth(RadioButton radioButton) {
        radioButton.setMinWidth(120);  // Set a fixed width for each button
    }

    // Method to check if the date is in the correct format
    private boolean isValidDate(String date) {
        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to check if the cost is in the correct format
    private boolean isValidCost(String cost) {
        try {
            // Try parsing the cost as a valid number (double)
            Double.parseDouble(cost);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to show an error dialog (keeping the dialog open)
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();  // Display error and keep the dialog open for correction
    }
}

