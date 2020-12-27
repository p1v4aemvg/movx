package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.ConfigurationControllers;
import com.by.movx.entity.CustomQuery;
import com.by.movx.event.StatNodeClickedEvent;
import com.by.movx.repository.CustomQueryRepository;
import com.by.movx.service.QueryEvaluator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.util.List;

public class DiagController {

    @Inject
    @Qualifier("statListView")
    ConfigurationControllers.View statListView;

    @Inject
    StatListController statListController;

    @FXML
    LineChart<String, Integer> bar;

    @FXML
    Label count;

    @FXML
    private ComboBox<CustomQuery> customQuery;

    @Inject
    CustomQueryRepository customQueryRepository;

    @Inject
    QueryEvaluator queryEvaluator;

    List<Object[]> stats;

    public void loadQ() {
        customQuery.setConverter(new StringConverter<CustomQuery>() {
            @Override
            public String toString(CustomQuery object) {
                return object.getName();
            }

            @Override
            public CustomQuery fromString(String string) {
                return null;
            }
        });
        customQuery.setItems(FXCollections.observableArrayList(customQueryRepository.findByQueryType(CustomQuery.QueryType.STAT)));
    }

    public void init() {
        bar.setVisible(true);
        bar.setMinWidth(stats.size() * 15);
        bar.setVerticalGridLinesVisible(true);
        bar.setHorizontalGridLinesVisible(true);

        XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
        stats.stream().forEach(e -> {
            String x = e[0].toString();
            Integer y = Integer.valueOf(e[1].toString());
            series.getData().add(new XYChart.Data<>(x, y));
        });

        bar.getData().clear();
        bar.setData(FXCollections.observableArrayList(series));

        for (final XYChart.Series<String, Integer> serie : bar.getData()) {
            for (final XYChart.Data<String, Integer> data : serie.getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setText(getStr(data.getYValue()) + " -> " + getStr(data.getXValue()));
                tooltip.setFont(new Font(15));
                Tooltip.install(data.getNode(), tooltip);

                data.getNode().setOnMouseClicked(e -> {
                    Common.getInstance().getEventBus().post(
                            new StatNodeClickedEvent(
                                    customQuery.getSelectionModel().getSelectedItem(),
                                    "'" + getStr(data.getXValue()) + "'"
                            )
                    );
                });

                data.getNode().setOnMouseEntered(event ->
                        data.getNode().setStyle("-fx-background-color: YELLOW;")
                );
                data.getNode().setOnMouseExited(event -> data.getNode().setStyle(""));
            }
        }
    }

    private String getStr(Object o) {
        if (o instanceof Integer)
            return o.toString();
        else if (o instanceof Double)
            return String.valueOf(((Double) o).intValue());
        return o.toString();
    }

    public void close() {
    }

    public void setStats(List<Object[]> stats) {
        this.stats = stats;
    }

    private Button anyButton() {
        Button b = new Button();

        b.setOnMouseClicked(e -> {
            ((Node) (e.getSource())).getScene().getWindow().hide();
        });
        return b;
    }

    @FXML
    public void query() {
        CustomQuery q = customQuery.getSelectionModel().getSelectedItem();
        if (q == null) return;

        if(q.getEntityType() == null) {
            setStats(queryEvaluator.getStats(q));
            init();
        } else {
            statListController.setCustomQuery(q);
            statListController.init();

            if (statListView.getView().getScene() != null)
                statListView.getView().getScene().setRoot(anyButton());

            Stage stage = new Stage();
            Scene scene = new Scene(statListView.getView());

            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        }
    }
}
