package com.bxtz;

import com.bxtz.entity.Message;
import com.bxtz.entity.Prompt;
import com.bxtz.utils.AIUtils;
import com.bxtz.utils.MarkdownUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalysePage {

    private Commons commons = new Commons();

    private BarChart<String, Number> timeBarChart;
    private PieChart categoryPieChart;

    private AIUtils aiUtils = new AIUtils();

    private MarkdownUtils markdownUtils = new MarkdownUtils();

    public VBox getAnalysisPage(ObservableList<Bill> bills) {
        HBox mainLayout = new HBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setStyle("-fx-background-color: #ffffff;");

        // 创建图表容器 VBox
        VBox chartsBox = new VBox(10);
        chartsBox.setPrefWidth(600); // 图表区域的宽度
        chartsBox.setPadding(new Insets(10));
        chartsBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");

        // 创建图表
        createBarChart(bills);
        createPieChart(bills);

        chartsBox.getChildren().addAll(timeBarChart, categoryPieChart, new Separator());

        // 创建 AI 聊天界面
        VBox aiBox = createAIChatBox(bills);
        aiBox.setPrefWidth(300); // 设置 AI 区域的宽度
        aiBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 1;");

        // 将图表和 AI 区域添加到主布局
        mainLayout.getChildren().addAll(chartsBox, aiBox);

        bills.addListener((ListChangeListener<Bill>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated() || change.wasReplaced()) {
                    createBarChart(bills);
                    createPieChart(bills);
                }
            }
        });

        for (Bill bill : bills) {
            bill.costProperty().addListener((observable, oldValue, newValue) -> {
                createBarChart(bills);
                createPieChart(bills);
            });

            bill.typeProperty().addListener((observable, oldValue, newValue) -> {
                createPieChart(bills);
            });
        }


        // 外层用 VBox 包裹，返回最终的布局
        VBox container = new VBox(mainLayout);
        container.setPadding(new Insets(10));
        return container;
    }



    private VBox createAIChatBox(ObservableList<Bill> bills) {
        ObjectMapper mapper = new ObjectMapper();

        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");
        box.setPrefWidth(400);

        Label label = new Label("💬 Ask AI about your expenses:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // 聊天内容区域
        VBox chatMessages = new VBox(8);
        chatMessages.setPrefWidth(500);
        chatMessages.setStyle("-fx-padding: 5;");

        ScrollPane scrollPane = new ScrollPane(chatMessages);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // 输入区域
        TextField inputField = new TextField();
        inputField.setPromptText("Ask a question (e.g., 哪天花费最多？)");
        inputField.setPrefWidth(400);

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        Runnable sendMessage = () -> {
            String question = inputField.getText().trim();
            ArrayList<Bill> billList = new ArrayList<>(bills);

            Prompt prompt = new Prompt();
            Message message = new Message();
            try {
                message.setBills(mapper.writeValueAsString(billList));
                message.setMsg(question);
                prompt.setPrompt(mapper.writeValueAsString(message));
            }catch (Exception exception){
                exception.printStackTrace();
            }

            if (!question.isEmpty()) {
                addMessage(chatMessages, "You: " + question, Pos.BASELINE_RIGHT, "#d0f0c0");
                inputField.clear();
                new Thread(() -> {
                    String reply = aiUtils.getResponse(prompt);
                    Platform.runLater(() -> {
                        addMarkdownMessage(chatMessages, "AI: " + reply, Pos.BASELINE_LEFT);
                        scrollPane.setVvalue(1.0); // 滚动到底部
                    });
                }).start();
            }
        };

        sendButton.setOnAction(e -> sendMessage.run());
        inputField.setOnAction(e -> sendMessage.run());

        box.getChildren().addAll(label, scrollPane, inputBox);
        return box;
    }


private void createBarChart(ObservableList<Bill> bills) {
    if (this.timeBarChart == null) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Total Cost");
        this.timeBarChart = new BarChart<>(xAxis, yAxis);
        this.timeBarChart.setTitle("Total Cost by Date");

        // 初始化一个 Series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Cost");
        this.timeBarChart.getData().add(series);
    }

    XYChart.Series<String, Number> series = this.timeBarChart.getData().get(0);
    series.getData().clear();

    Map<String, Double> dateCostMap = new HashMap<>();

    for (Bill bill : bills) {
        String date = bill.getDate().split(" ")[0];
        double cost = Double.parseDouble(bill.getCost().replace(" RMB", ""));
        dateCostMap.put(date, dateCostMap.getOrDefault(date, 0.0) + cost);
    }

    for (Map.Entry<String, Double> entry : dateCostMap.entrySet()) {
        XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
        Platform.runLater(() -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #a8d5ba;"); // 淡绿色
                }
            });
        series.getData().add(data);
    }
}

    private void createPieChart(ObservableList<Bill> bills) {
        if (this.categoryPieChart == null) {
            this.categoryPieChart = new PieChart();
            this.categoryPieChart.setTitle("Total Cost by Category");
        }
        categoryPieChart.setPrefWidth(800); // 设置宽度
        categoryPieChart.setPrefHeight(800);

        Map<String, Double> categoryCostMap = new HashMap<>();

        for (Bill bill : bills) {
            String type = bill.getType();
            double cost = Double.parseDouble(bill.getCost().replace(" RMB", ""));
            categoryCostMap.put(type, categoryCostMap.getOrDefault(type, 0.0) + cost);
        }

        ObservableList<PieChart.Data> dataList = this.categoryPieChart.getData();
        dataList.clear();

        for (Map.Entry<String, Double> entry : categoryCostMap.entrySet()) {
            dataList.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    private void addMessage(VBox chatMessages, String message, Pos alignment, String bgColor) {
        Text text = new Text(message);
        text.setWrappingWidth(450); // 限制宽度以自动换行
        text.setStyle("-fx-font-size: 13px;");

        TextFlow bubble = new TextFlow(text);
        bubble.setPadding(new Insets(8));
        bubble.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");
        bubble.setMaxWidth(480);

        HBox container = new HBox(bubble);
        container.setAlignment(alignment);
        chatMessages.getChildren().add(container);
    }

    private void addMarkdownMessage(VBox chatMessages, String markdown, Pos alignment) {
        String html = markdownUtils.markdownToHtml(markdown);
        String htmlPage = """
            <html>
            <head>
                <style>
                    body { font-family: Arial; padding: 10px; margin: 0; }
                    pre { background: #f0f0f0; padding: 5px; border-radius: 3px; }
                    code { font-family: monospace; }
                </style>
            </head>
            <body>%s</body>
            </html>
            """.formatted(html);

        WebView webView = new WebView();
        webView.setMaxWidth(400);  // 限制宽度，防止聊天框拉太宽
        WebEngine engine = webView.getEngine();
        engine.loadContent(htmlPage);

        HBox container = new HBox(webView);
        container.setAlignment(alignment);
        chatMessages.getChildren().add(container);
    }

}