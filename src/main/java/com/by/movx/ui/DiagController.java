package com.by.movx.ui;

import com.by.movx.ConfigurationControllers;
import com.by.movx.repository.ActorRepository;
import com.by.movx.repository.CountryRepository;
import com.by.movx.repository.FilmRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.util.List;

public class DiagController {

    @Inject
    @Qualifier("statActorsView")
    ConfigurationControllers.View statActorsView;

    @Inject
    StatActorsController statActorsController;

    @Inject
    private ActorRepository actorRepository;


    @Inject
    private FilmRepository filmRepository;


    @Inject
    private CountryRepository countryRepository;

    @FXML
    LineChart<String, Integer> bar;

    @FXML
    Label count;

    List<Object[]> stats;

    int offset = 0, limit = 10;

    public void init() {

        bar.setVerticalGridLinesVisible(true);
        bar.setHorizontalGridLinesVisible(true);

        XYChart.Series<String, Integer> series = new XYChart.Series<String, Integer>();
        series.setName("Wanderlust");
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
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }

    private String getStr(Object o) {
        if(o instanceof Integer )
            return o.toString();
        else if(o instanceof Double)
            return String.valueOf(((Double) o).intValue());
        return o.toString();
    }

    public void close() {
    }

    public void setStats(List<Object[]> stats) {
        this.stats = stats;
    }

    @FXML
    public void diagByYear() throws Exception {
        setStats(filmRepository.yearStats());
        init();
    }

    @FXML
    public void diagByMark() throws Exception {
        setStats(filmRepository.markStats());
        init();
    }

    @FXML
    public void diagByCountry() throws Exception {
        setStats(countryRepository.loadCountryStat());
        init();
    }

    @FXML
    public void diagBy1stLetter() throws Exception {
        setStats(filmRepository.filmsBy1stLetter());
        init();
    }


    @FXML
    public void diagByPeriods() throws Exception {
        setStats(filmRepository.periodStats());
        init();
    }

    @FXML
    public void diagByActor() throws Exception {
        statActorsController.init();

        if (statActorsView.getView().getScene() != null)
            statActorsView.getView().getScene().setRoot(anyButton());

        Stage stage = new Stage();
        Scene scene = new Scene(statActorsView.getView());

        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private Button anyButton() {
        Button b = new Button();

        b.setOnMouseClicked(e -> {
            ((Node)(e.getSource())).getScene().getWindow().hide();
        });
        return b;
    }
}
