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
// import java.util.regex.Matcher; // regex.Matcher and Pattern not used
// import java.util.regex.Pattern;

public class Commons {
    /**
     * Updates the total cost label by summing all bill amounts in the list.
     *
     * @param bills The observable list of bills to calculate from
     * @param totalCost The label to display the calculated total
     */
    public void updateTotalCost(ObservableList<Bill> bills, Label totalCost) {
        double sum = 0.0;
        for (Bill b : bills) {
            if ("".equals(b.getCost()) || b.getCost() == null) { // Added null check for safety
                continue;
            }
            // Ensure " RMB" is removed before parsing, handle cases where it might be missing
            String costString = b.getCost().toLowerCase().replace("rmb", "").trim();
            if (costString.isEmpty()) {
                continue;
            }
            try {
                sum += Double.parseDouble(costString);
            } catch (NumberFormatException e) {
                System.err.println("Could not parse cost: " + b.getCost());
                // Optionally handle this error more gracefully
            }
        }
        totalCost.setText("Total Cost: " + String.format("%.2f", sum) + " RMB"); // Format to 2 decimal places
    }

    // ... (styleButton methods remain the same) ...
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

        // Use original cost string for editing, which might include " RMB"
        TextField dateField = new TextField(bill.getDate());
        TextField nameField = new TextField(bill.getName());
        TextField detailsField = new TextField(bill.getDetails());
        TextField costField = new TextField(bill.getCost()); // Keep " RMB" if present for display
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
        styleButton4(deleteBtn); //
        deleteBtn.setOnAction(event -> { // Changed variable name from edited to event
            ObservableList<Bill> bills = table.getItems();
            bills.remove(bill);
            // No need to call table.setItems(bills) if bills is the original list from table.getItems()
            updateTotalCost(bills, totalCost);
            table.refresh();
            dialog.close(); // Close dialog after delete
        });
        grid.add(deleteBtn, 1, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String date = dateField.getText();
                String costStr = costField.getText(); // Cost string from field

                if (!isValidDate(date)) {
                    showErrorDialog("Format error: Invalid date format. Please use yyyy-MM-dd HH:mm.");
                    return null;
                }
                // For cost, check if it's a valid number part, then ensure " RMB" is appended
                String numericCostPart = costStr.toLowerCase().replace("rmb", "").trim();
                if (!isValidCost(numericCostPart)) { // Validate only the numeric part
                    showErrorDialog("Format error: Invalid cost format. Please enter a valid number.");
                    return null;
                }
                // Ensure " RMB" suffix, avoid double suffix
                if (!numericCostPart.isEmpty() && !costStr.toLowerCase().endsWith("rmb")) {
                    costStr = numericCostPart + " RMB";
                } else if (numericCostPart.isEmpty()) {
                    costStr = "0 RMB"; // Default if empty after stripping
                }


                return new Bill(
                        date,
                        nameField.getText(),
                        detailsField.getText(),
                        costStr, // Use potentially modified costStr
                        typeField.getText()
                );
            }
            return null;
        });

        Optional<Bill> result = dialog.showAndWait();

        result.ifPresent(editedBill -> { // Changed variable name
            bill.dateProperty().set(editedBill.getDate());
            bill.nameProperty().set(editedBill.getName());
            bill.detailsProperty().set(editedBill.getDetails());
            bill.costProperty().set(editedBill.getCost());
            bill.typeProperty().set(editedBill.getType());
            updateTotalCost(table.getItems(), totalCost);
            table.refresh();
        });
    }

    public Button createExportButton(TableView<Bill> table, Label totalCost, Stage stage) {
        Button exportBtn = new Button("Download Bills");
        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV file"); // Corrected title
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                // Ensure filename ends with .csv
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                BillUtils.exportToCSV(table.getItems(), file);
            }
        });
        return exportBtn;
    }

    public Button createImportButton(TableView<Bill> table, Label totalCost, Stage stage) {
        Button importBtn = new Button("Upload Bills");
        importBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV file"); // Corrected title
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                List<Bill> newBills = BillUtils.importFromCSV(file);
                ObservableList<Bill> bills = FXCollections.observableArrayList(newBills);
                table.setItems(bills); // This replaces all existing bills. If appending is desired, use table.getItems().addAll(bills);
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
        TextField costField = new TextField(""); // User enters only the number

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton foodButton = new RadioButton("Food");
        foodButton.setToggleGroup(typeGroup);
        RadioButton shoppingButton = new RadioButton("Shopping");
        shoppingButton.setToggleGroup(typeGroup);
        RadioButton entertainmentButton = new RadioButton("Entertainment");
        entertainmentButton.setToggleGroup(typeGroup);
        RadioButton othersButton = new RadioButton("Others");
        othersButton.setToggleGroup(typeGroup);
        foodButton.setSelected(true);

        styleRadioButton(foodButton); //
        styleRadioButton(shoppingButton); //
        styleRadioButton(entertainmentButton); //
        styleRadioButton(othersButton); //

        VBox typeBox = new VBox(10, foodButton, shoppingButton, entertainmentButton, othersButton);
        typeBox.setSpacing(5); // Consistent spacing

        setRadioButtonWidth(foodButton); //
        setRadioButtonWidth(shoppingButton); //
        setRadioButtonWidth(entertainmentButton); //
        setRadioButtonWidth(othersButton); //


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
        grid.add(new Label("Cost (enter number only):"), 0, 3); // Clarified label
        grid.add(costField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String date = dateField.getText();
                String costStr = costField.getText().trim(); // Numeric part of cost

                if (!isValidDate(date)) {
                    showErrorDialog("Format error: Invalid date format. Please use yyyy-MM-dd HH:mm.");
                    return null;
                }

                if (!isValidCost(costStr)) { // Validate the numeric part
                    showErrorDialog("Format error: Invalid cost format. Please enter a valid number for cost.");
                    return null;
                }

                String finalCost = costStr.isEmpty() ? "0 RMB" : costStr + " RMB"; // Append RMB

                RadioButton selectedRadioButton = (RadioButton) typeGroup.getSelectedToggle();
                String type = selectedRadioButton != null ? selectedRadioButton.getText() : "Others"; // Default if somehow none selected

                return new Bill(
                        date,
                        nameField.getText(),
                        detailsField.getText(),
                        finalCost,
                        type
                );
            }
            return null;
        });

        Optional<Bill> result = dialog.showAndWait();

        result.ifPresent(newBill -> { // Changed variable name
            ObservableList<Bill> bills = table.getItems();
            bills.add(newBill);
            // table.setItems(bills); // Not necessary if bills is obtained from getItems() and modified
            updateTotalCost(bills, totalCost);
            table.refresh();
        });
    }

    private void styleRadioButton(RadioButton radioButton) {
        radioButton.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-padding: 5;");
    }

    private void setRadioButtonWidth(RadioButton radioButton) {
        radioButton.setMinWidth(120);
    }

    // Make public for testing
    public boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(date.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Make public for testing
    public boolean isValidCost(String cost) {
        if (cost == null || cost.trim().isEmpty()) {
            return false; // Or treat empty as 0, depending on requirements
        }
        try {
            Double.parseDouble(cost.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}