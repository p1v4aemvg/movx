package com.by.movx.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;


import java.math.BigInteger;
import java.util.List;


public class DiagController {

    @FXML
    LineChart<String, Integer> bar;

    @FXML
    Label count;

    List<Object[]> stats;

    public void init() {

        bar.setVerticalGridLinesVisible(true);
        bar.setHorizontalGridLinesVisible(true);

        XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
        series.setName("Wanderlust");
        stats.stream().forEach(e -> {
            String x = e[0].toString();
            Integer y = ((BigInteger)e[1]).intValue();
            series.getData().add(new XYChart.Data<String, Integer>(x, y));
        });

        bar.getData().clear();
        bar.setData(FXCollections.observableArrayList(series));

//        for (XYChart.Series<String,Integer> serie: bar.getData()){
//            for (XYChart.Data<String, Integer> item: serie.getData()){
//                item.getNode().setOnMousePressed((MouseEvent event) -> {
//                    System.out.println("you clicked "+item.toString());
//                    count.setText(item.getYValue().toString());
//                });
//            }
//        }

        for (final XYChart.Series<String, Integer> serie : bar.getData()) {
            for (final XYChart.Data<String, Integer> data : serie.getData()) {
                Tooltip tooltip = new Tooltip();
                Object y = data.getYValue();
                if(y instanceof Integer )
                   tooltip.setText(y.toString());
                else if(y instanceof Double)
                    tooltip.setText(String.valueOf(((Double) y).intValue()));
                Tooltip.install(data.getNode(), tooltip);
            }
        }

    }

    public void close() {
    }

    public void setStats(List<Object[]> stats) {
        this.stats = stats;
    }
}
