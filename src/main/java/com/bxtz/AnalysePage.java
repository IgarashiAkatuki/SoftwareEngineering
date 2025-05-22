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
        VBox vbox = new VBox(10);

        // 创建图表
        createBarChart(bills);
        createPieChart(bills);

        // 创建AI对话框
        VBox aiBox = createAIChatBox(bills);

        vbox.getChildren().addAll(timeBarChart, categoryPieChart, new Separator(), aiBox);

        bills.addListener((ListChangeListener<Bill>) change -> {
            createBarChart(bills);
            createPieChart(bills);
            vbox.getChildren().clear();
            vbox.getChildren().addAll(timeBarChart, categoryPieChart, new Separator(), aiBox);
        });

        return vbox;
    }

    private VBox createAIChatBox(ObservableList<Bill> bills) {
        ObjectMapper mapper = new ObjectMapper();

        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

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

    // 创建按时间的柱状图
    private void createBarChart(ObservableList<Bill> bills) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Total Cost");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Total Cost by Date");


        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Cost");

        Map<String, Double> dateCostMap = new HashMap<>();

        // 遍历账单数据，按日期统计费用
        for (Bill bill : bills) {
            String date = bill.getDate().split(" ")[0]; // 取日期部分
            double cost = Double.parseDouble(bill.getCost().replace(" RMB", ""));
            dateCostMap.put(date, dateCostMap.getOrDefault(date, 0.0) + cost);
        }

        // 填充柱状图数据
        for (Map.Entry<String, Double> entry : dateCostMap.entrySet()) {
//            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());

            // 使用 Platform.runLater 确保在渲染完毕后修改颜色
            Platform.runLater(() -> {
                // 设置柱状图的颜色
                data.getNode().setStyle("-fx-bar-fill: #65c43d;");  // 设置柱形的颜色，这里使用了番茄红
            });

            series.getData().add(data);

        }

        barChart.getData().add(series);
        System.out.println(series);
        this.timeBarChart = barChart;
    }

    // 创建按分类的环状图
    private void createPieChart(ObservableList<Bill> bills) {
        PieChart pieChart = new PieChart();
        Map<String, Double> categoryCostMap = new HashMap<>();

        // 遍历账单数据，按类别统计费用
        for (Bill bill : bills) {
            String type = bill.getType();
            double cost = Double.parseDouble(bill.getCost().replace(" RMB", ""));
            categoryCostMap.put(type, categoryCostMap.getOrDefault(type, 0.0) + cost);
        }

        // 填充环状图数据
        for (Map.Entry<String, Double> entry : categoryCostMap.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChart.getData().add(data);
        }

        pieChart.setTitle("Total Cost by Category");
        this.categoryPieChart = pieChart;
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

