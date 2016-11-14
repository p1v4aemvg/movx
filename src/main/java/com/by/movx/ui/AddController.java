package com.by.movx.ui;

import com.by.movx.entity.*;
import com.by.movx.repository.CountryRepository;
import com.by.movx.repository.FilmRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class AddController {

    @FXML
    TextField name, enname, year;

    @FXML
    Slider duration;

    @FXML
    private ComboBox<Country> country;

    @FXML
    private ChoiceBox<Film.Type> type;

    @Inject
    FilmRepository filmRepository;

    @Inject
    CountryRepository countryRepository;

    private Film film, parent;

    @FXML
    private CheckBox countInStats;

    public void init() {
        film = new Film();
        film.setCountries(new HashSet<>());
        name.clear();
        enname.clear();
        year.clear();


        ObservableList<Country> countries = FXCollections.observableArrayList((List<Country>) countryRepository.findAll());
        country.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country object) {
                return object.getName();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        });

        ObservableList<Film.Type> types = FXCollections.observableArrayList(Film.Type.values());
        type.setConverter(new StringConverter<Film.Type>() {
            @Override
            public String toString(Film.Type object) {
                return object.getName();
            }

            @Override
            public Film.Type fromString(String string) {
                return null;
            }
        });

        country.setItems(countries);
        type.setItems(types);


        checkParent();
    }

    private void checkParent() {
        if(parent != null) {
            film.setType(parent.getType());
            type.setValue(parent.getType());

            film.setDuration(parent.getDuration());
            duration.setValue((double)parent.getDuration().ordinal());

            film.setCountries(parent.getCountries().stream().map(c -> {
                Country newC = new Country();
                newC.setId(c.getId());
                return newC;
            }).collect(Collectors.toSet()));
        }
    }

    @FXML
    public void add() throws Exception {
        film.setName(name.getText());
        film.setEnName(enname.getText().isEmpty() ? null : enname.getText());
        film.setYear(Integer.valueOf(year.getText()));
        film.setType(type.getSelectionModel().getSelectedItem());
        film.setMark(0);
        film.setDuration(Film.Duration.of((int) duration.getValue()));
        film.setParent(parent);
        film.setCountInStat(countInStats.isSelected());
        film = filmRepository.save(film);
        film.setDescription(new FilmDescription(film));
        filmRepository.save(film);

        parent = null;
    }

    @FXML
    public void countryAdded() {
        Country c = country.getSelectionModel().getSelectedItem();
        if (c != null)
            film.getCountries().add(c);
    }

    public void setParent(Film parent) {
        this.parent = parent;
    }
}
