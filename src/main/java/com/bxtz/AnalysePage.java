package com.bxtz;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class AnalysePage {

    private Commons commons = new Commons();

    // 创建分析界面
    public VBox getAnalysisPage(ObservableList<Bill> bills) {
        VBox vbox = new VBox(10);

        // 1. 按时间的柱状图
        BarChart<String, Number> timeBarChart = createBarChart(bills);

        // 2. 按分类的环状图
        PieChart categoryPieChart = createPieChart(bills);

        vbox.getChildren().addAll(timeBarChart, categoryPieChart);
        return vbox;
    }

    // 创建按时间的柱状图
    private BarChart<String, Number> createBarChart(ObservableList<Bill> bills) {
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
                data.getNode().setStyle("-fx-bar-fill: #47eaff;");  // 设置柱形的颜色，这里使用了番茄红
            });

            series.getData().add(data);

        }

        barChart.getData().add(series);
        return barChart;
    }

    // 创建按分类的环状图
    private PieChart createPieChart(ObservableList<Bill> bills) {
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
        return pieChart;
    }
}

