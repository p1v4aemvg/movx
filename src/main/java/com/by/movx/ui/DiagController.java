package com.by.movx.ui;

import com.by.movx.repository.ActorRepository;
import com.by.movx.repository.CountryRepository;
import com.by.movx.repository.FilmRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.springframework.data.domain.PageRequest;


import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;


public class DiagController {

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
        offset = 0;
        setStats(actorRepository.actorsByRoles(new PageRequest(offset, limit)));
        init();
    }

    @FXML
    public void onPrev() {
        offset--;
        setStats(actorRepository.actorsByRoles(new PageRequest(offset, limit)));
        init();
    }

    @FXML
    public void onNext() {
        offset++;
        setStats(actorRepository.actorsByRoles(new PageRequest(offset, limit)));
        init();
    }
}
