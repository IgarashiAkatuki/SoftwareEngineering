package com.bxtz;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Main application class for the Bills Manager.
 * It provides a UI to manage, display, and analyze bill entries.
 *
 * Created by Group 94.
 */
public class Main extends Application {

    /** Table view to display bill entries */
    private TableView<Bill> table = new TableView<>();

    /** Label to show total cost */
    private Label totalCostLabel = new Label("Total Cost: 0 RMB");

    /** Shared utility class for styling and dialogs */
    private Commons commons = new Commons();

    /**
     * Main entry point of the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application and initializes the UI.
     *
     * @param primaryStage the main application window
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bills Manager");

        // Header title
        Label title = new Label("Bills Manager by Group 94");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.WHITE);
        HBox header = new HBox(title);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        // Create main content pages
        VBox detailsPage = getDetailsPage(primaryStage);
        VBox analysisPage = getAnalysisPage();
        VBox userManualPage = getUserManualPage();


        // Navigation buttons
        Button btnDetails = new Button("Details");
        Button btnAnalysis = new Button("Analysis");
        Button btnManual = new Button("User Manual");

        // Shadow effect for button highlighting
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setOffsetX(4);
        shadow.setOffsetY(4);
        shadow.setRadius(8);

        // Style navigation buttons
        btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 5px; -fx-padding: 10px;");
        btnAnalysis.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 5px; -fx-padding: 10px;");
        btnManual.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 5px; -fx-padding: 10px;");
        btnDetails.setEffect(shadow); // Highlight default page

        // Functional buttons
        Button uploadBtn = commons.createImportButton(table, totalCostLabel, primaryStage);
        Button downloadBtn = commons.createExportButton(table, totalCostLabel, primaryStage);
        Button addBtn = new Button("Add Bill");

        // Style them
        commons.styleButton1(uploadBtn);
        commons.styleButton2(downloadBtn);
        commons.styleButton2(addBtn);

        // Button bar
        HBox bar = new HBox(10, btnDetails, btnAnalysis,btnManual);
        bar.setPadding(new Insets(10));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #eeeeee;");

        // Switchable content pane
        StackPane contentPane = new StackPane(detailsPage, analysisPage);
        analysisPage.setVisible(false); // Show only details page by default
        contentPane.getChildren().add(userManualPage);
        userManualPage.setVisible(false);

        // Switch to details page
        btnDetails.setOnAction(e -> {
            detailsPage.setVisible(true);
            analysisPage.setVisible(false);
            userManualPage.setVisible(false);
            btnDetails.setEffect(shadow);
            btnAnalysis.setEffect(null);
            btnManual.setEffect(null);
        });

        // Switch to analysis page
        btnAnalysis.setOnAction(e -> {
            detailsPage.setVisible(false);
            analysisPage.setVisible(true);
            userManualPage.setVisible(false);
            btnDetails.setEffect(null);
            btnAnalysis.setEffect(shadow);
            btnManual.setEffect(null);
        });

        // Switch to Manual Page
        btnManual.setOnAction(e -> {
            detailsPage.setVisible(false);
            analysisPage.setVisible(false);
            userManualPage.setVisible(true);
            btnDetails.setEffect(null);
            btnAnalysis.setEffect(null);
            btnManual.setEffect(shadow);

        });

        // Keep shadow effect on button release
        btnDetails.setOnMouseReleased(e -> {
            btnDetails.setEffect(detailsPage.isVisible() ? shadow : null);
        });

        btnAnalysis.setOnMouseReleased(e -> {
            btnAnalysis.setEffect(analysisPage.isVisible() ? shadow : null);
        });
        btnManual.setOnMouseReleased(e -> {
            btnManual.setEffect(userManualPage.isVisible() ? shadow : null);
        });

        VBox root = new VBox(header, bar, contentPane);
        VBox.setVgrow(contentPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a reusable column for the bill table.
     *
     * @param title    the column header
     * @param property the bill property (e.g., "date", "name", etc.)
     * @param style    the CSS style to apply
     * @return the configured table column
     */
    private TableColumn<Bill, String> createColumn(String title, String property, String style) {
        TableColumn<Bill, String> col = new TableColumn<>(title);
        col.setStyle(style);
        col.setCellValueFactory(cell -> switch (property) {
            case "date" -> cell.getValue().dateProperty();
            case "name" -> cell.getValue().nameProperty();
            case "details" -> cell.getValue().detailsProperty();
            case "cost" -> cell.getValue().costProperty();
            case "type" -> cell.getValue().typeProperty();
            default -> throw new IllegalArgumentException("Invalid property: " + property);
        });
        return col;
    }

    /**
     * Builds the bill details page.
     *
     * @param primaryStage the main stage for file dialogs
     * @return a VBox containing the details view
     */
    private VBox getDetailsPage(Stage primaryStage) {
        // Top action buttons
        HBox topButtons = new HBox(15);
        topButtons.setPadding(new Insets(15));
        topButtons.setStyle("-fx-background-color: #ecf0f1;");

        Button uploadBtn = commons.createImportButton(table, totalCostLabel, primaryStage);
        Button downloadBtn = commons.createExportButton(table, totalCostLabel, primaryStage);
        Button addBtn = new Button("Add Bill");

        addBtn.setOnAction(e -> commons.showAddDialog(table, totalCostLabel));

        commons.styleButton1(uploadBtn);
        commons.styleButton2(downloadBtn);
        commons.styleButton2(addBtn);

        totalCostLabel.setFont(Font.font(14));
        topButtons.getChildren().addAll(uploadBtn, downloadBtn, addBtn, totalCostLabel);

        // Setup table
        table.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPadding(new Insets(10));

        String headerStyle = "-fx-background-color: #bdc3c7; -fx-font-weight: bold;";

        TableColumn<Bill, String> dateCol = createColumn("Date", "date", headerStyle);
        TableColumn<Bill, String> nameCol = createColumn("Name", "name", headerStyle);
        TableColumn<Bill, String> detailCol = createColumn("Details", "details", headerStyle);
        TableColumn<Bill, String> costCol = createColumn("Cost", "cost", headerStyle);
        TableColumn<Bill, String> typeCol = createColumn("Type", "type", headerStyle);

        // Add icons to type column
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label label = new Label(item);
                    ImageView imageView = null;
                    switch (item) {
                        case "Food" -> imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/food_icon.png"))));
                        case "Shopping" -> imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/shopping_icon.png"))));
                        case "Entertainment" -> imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/game_icon.png"))));
                        case "Others" -> imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/others_icon.png"))));
                    }
                    if (imageView != null) {
                        imageView.setFitWidth(16);
                        imageView.setFitHeight(16);
                        imageView.setPreserveRatio(true);
                        label.setGraphic(imageView);
                    }
                    label.setGraphicTextGap(10);
                    setGraphic(label);
                }
            }
        });

        // Add edit column
        TableColumn<Bill, Void> editCol = new TableColumn<>("Edit");
        editCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                commons.styleButton1(btn);
                btn.setOnAction(event -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    commons.showEditDialog(bill, table, totalCostLabel);
                    System.out.println("Edit bill: " + bill.getName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Add columns and data
        table.getColumns().addAll(dateCol, nameCol, detailCol, costCol, typeCol, editCol);

        ObservableList<Bill> data = FXCollections.observableArrayList(
                new Bill("2025-02-01 12:00", "Mei Tuan", "Takeout", "50 RMB", "Food"),
                new Bill("2025-02-02 20:45", "Amazon", "Shopping", "120 RMB", "Shopping"),
                new Bill("2025-02-06 01:00", "ApplePay", "Online Pay", "648 RMB", "Entertainment")
        );
        table.setItems(data);
        commons.updateTotalCost(data, totalCostLabel);

        VBox detailsPage = new VBox();
        detailsPage.getChildren().addAll(topButtons, table);
        detailsPage.setStyle("-fx-background-color: #f9f9f9;");
        return detailsPage;
    }

    /**
     * Builds the analysis page.
     *
     * @return a VBox containing analysis charts and summary
     */
    private VBox getAnalysisPage() {
        Label analysisTitle = new Label("Bill  Analysis");
        analysisTitle.setFont(Font.font("Arial", 20));
        analysisTitle.setStyle("-fx-font-weight: bold;");
        analysisTitle.setTextFill(Color.BLACK);

        AnalysePage analysePage = new AnalysePage();
        VBox analysisContent = analysePage.getAnalysisPage(table.getItems());

        VBox analysisPageLayout = new VBox(20, analysisTitle, analysisContent);
        analysisPageLayout.setPadding(new Insets(20));
        analysisPageLayout.setStyle("-fx-background-color: #f9f9f9;");

        return analysisPageLayout;
    }

    /**
     * Builds the user manual page.
     *
     * @return a VBox containing the user manual content
     */
    private VBox getUserManualPage() {
        Label title = new Label("User Manual");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");
        title.setTextFill(Color.BLACK);

        // User manual content
        Label content = new Label("""
        Welcome to the Bills Manager application!
        
        Features:
        - Details Page: View, add, edit, and manage your bills.
        - Analysis Page: Analyze your expenses using charts.
        - User Manual: Get started and learn how to use the application.
        
        How to Use:
        1. Add a Bill: Click 'Add Bill' to input a new bill entry.
        2. Import/Export: Use the upload/download buttons to manage data files.
        3. AI Type Analysis: Analyze bill types using advanced algorithms.
        4. Edit Bills: Use the 'Edit' button in the Details page.
        
        For support, contact:  support@billsmanager.com
        """);
        content.setWrapText(true);
        content.setFont(Font.font("Arial", 14));
        content.setTextFill(Color.DARKBLUE);

        VBox manualPage = new VBox(20, title, content);
        manualPage.setPadding(new Insets(20));
        manualPage.setStyle("-fx-background-color: #f9f9f9;");
        manualPage.setAlignment(Pos.TOP_LEFT);

        return manualPage;
    }

}