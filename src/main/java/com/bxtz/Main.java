package com.bxtz;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.*;

public class Main extends Application {

    private TableView<Bill> table = new TableView<>();
    private Label totalCostLabel = new Label("Total Cost: 0 RMB");
    private Commons commons = new Commons();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bills Manager");

        Label title = new Label("Bills Manager by Group 94");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.WHITE);
        HBox header = new HBox(title);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        VBox detailsPage = getDetailsPage(primaryStage);
        VBox analysisPage = getAnalysisPage();

        Button btnDetails = new Button("Details");
        Button btnAnalysis = new Button("Analysis");

        // Create shadow effect for buttons
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setOffsetX(4);
        shadow.setOffsetY(4);
        shadow.setRadius(8);

        // Apply styles to Details and Analysis buttons to make them distinct
        btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 5px; -fx-padding: 10px;");
        btnAnalysis.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 5px; -fx-padding: 10px;");

        // Apply shadow effect on Details button initially
        btnDetails.setEffect(shadow);

        // Other buttons with default style
        Button uploadBtn = commons.createImportButton(table, totalCostLabel, primaryStage);
        Button downloadBtn = commons.createExportButton(table, totalCostLabel, primaryStage);
        Button analyzeBtn = new Button("AI Type Analysis");
        Button addBtn = new Button("Add Bill");

        commons.styleButton1(uploadBtn);
        commons.styleButton2(downloadBtn);
        commons.styleButton3(analyzeBtn);
        commons.styleButton2(addBtn);

        HBox bar = new HBox(10, btnDetails, btnAnalysis);
        bar.setPadding(new Insets(10));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #eeeeee;");

        StackPane contentPane = new StackPane(detailsPage, analysisPage);
        analysisPage.setVisible(false); // 默认只显示详情页

        // Button action to switch between pages
        btnDetails.setOnAction(e -> {
            detailsPage.setVisible(true);
            analysisPage.setVisible(false);
            btnDetails.setEffect(shadow);  // Add shadow to Details button
            btnAnalysis.setEffect(null);   // Remove shadow from Analysis button
        });

        btnAnalysis.setOnAction(e -> {
            detailsPage.setVisible(false);
            analysisPage.setVisible(true);
            btnDetails.setEffect(null);    // Remove shadow from Details button
            btnAnalysis.setEffect(shadow); // Add shadow to Analysis button
        });

        // Button press effect
        btnDetails.setOnMousePressed(e -> {
            btnDetails.setEffect(shadow);
            btnAnalysis.setEffect(null);
        });

        btnAnalysis.setOnMousePressed(e -> {
            btnAnalysis.setEffect(shadow);
            btnDetails.setEffect(null);
        });

        // Button release effect
        btnDetails.setOnMouseReleased(e -> {
            if (detailsPage.isVisible()) {
                btnDetails.setEffect(shadow);
            } else {
                btnDetails.setEffect(null);
            }
        });

        btnAnalysis.setOnMouseReleased(e -> {
            if (analysisPage.isVisible()) {
                btnAnalysis.setEffect(shadow);
            } else {
                btnAnalysis.setEffect(null);
            }
        });

        VBox root = new VBox(header, bar, contentPane);
        VBox.setVgrow(contentPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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

    private VBox getDetailsPage(Stage primaryStage) {
        HBox topButtons = new HBox(15);
        topButtons.setPadding(new Insets(15));
        topButtons.setStyle("-fx-background-color: #ecf0f1;");

        Button uploadBtn = commons.createImportButton(table, totalCostLabel, primaryStage);
        Button downloadBtn = commons.createExportButton(table, totalCostLabel, primaryStage);
        Button analyzeBtn = new Button("AI Type Analysis");
        Button addBtn = new Button("Add Bill");

        addBtn.setOnAction((e) -> {
            commons.showAddDialog(table, totalCostLabel);
        });
        commons.styleButton1(uploadBtn);
        commons.styleButton2(downloadBtn);
        commons.styleButton3(analyzeBtn);
        commons.styleButton2(addBtn);

        totalCostLabel.setFont(Font.font(14));
        topButtons.getChildren().addAll(uploadBtn, downloadBtn, addBtn, analyzeBtn, totalCostLabel);

        table.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPadding(new Insets(10));

        String headerStyle = "-fx-background-color: #bdc3c7; -fx-font-weight: bold;";

        TableColumn<Bill, String> dateCol = createColumn("Date", "date", headerStyle);
        TableColumn<Bill, String> nameCol = createColumn("Name", "name", headerStyle);
        TableColumn<Bill, String> detailCol = createColumn("Details", "details", headerStyle);
        TableColumn<Bill, String> costCol = createColumn("Cost", "cost", headerStyle);
        TableColumn<Bill, String> typeCol = createColumn("Type", "type", headerStyle);

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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(dateCol, nameCol, detailCol, costCol, typeCol, editCol);

        ObservableList<Bill> data = FXCollections.observableArrayList(
                new Bill("2025-02-01 12:00", "Mei Tuan", "Takeout", "50 RMB", "Food"),
                new Bill("2025-02-02 20:45", "Amazon", "Shopping", "120 RMB", "Shopping"),
                new Bill("2025-02-06 01:00", "ApplePay", "Online Pay", "648 RMB", "Game")
        );
        table.setItems(data);
        commons.updateTotalCost(data, totalCostLabel);

        VBox detailsPage = new VBox();
        detailsPage.getChildren().addAll(topButtons, table);
        detailsPage.setStyle("-fx-background-color: #f9f9f9;");
        return detailsPage;
    }

    private VBox getAnalysisPage() {
        // 创建分析页面标题
        Label analysisTitle = new Label("Bill  Analysis");
        analysisTitle.setFont(Font.font("Arial", 20));
        analysisTitle.setStyle("-fx-font-weight: bold;");
        analysisTitle.setTextFill(Color.BLACK);

        // 创建分析界面，传入账单数据
        AnalysePage analysePage = new AnalysePage();
        VBox analysisContent = analysePage.getAnalysisPage(table.getItems());

        // 布局分析页面
        VBox analysisPageLayout = new VBox(20, analysisTitle, analysisContent);
        analysisPageLayout.setPadding(new Insets(20));
        analysisPageLayout.setStyle("-fx-background-color: #f9f9f9;");

        return analysisPageLayout;
    }

}