package com.by.movx.ui;

import com.by.movx.entity.*;
import com.by.movx.repository.CountryRepository;
import com.by.movx.repository.FilmRepository;
import com.by.movx.repository.TypeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class AddController {

    @FXML
    TextField name;

    @FXML
    TextField enname;

    @FXML
    TextField year;

    @FXML
    private ComboBox<Country> country;

    @FXML
    private ChoiceBox<Type> type;

    @Inject
    FilmRepository filmRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    TypeRepository typeRepository;

    private Film film;


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

        ObservableList<Type> types = FXCollections.observableArrayList((List<Type>) typeRepository.findAll());
        type.setConverter(new StringConverter<Type>() {
            @Override
            public String toString(Type object) {
                return object.getName();
            }

            @Override
            public Type fromString(String string) {
                return null;
            }
        });

        country.setItems(countries);
        type.setItems(types);
    }

    @FXML
    public void add() throws Exception {
        film.setName(name.getText());
        film.setEnName(enname.getText().isEmpty() ? null : enname.getText());
        film.setYear(Integer.valueOf(year.getText()));
        film.setType(type.getSelectionModel().getSelectedItem());
        film.setMark(0);
        film = filmRepository.save(film);
        film.setDescription(new FilmDescription(film));
        filmRepository.save(film);
    }

    @FXML
    public void countryAdded() {
        Country c = country.getSelectionModel().getSelectedItem();
        if(c != null)
            film.getCountries().add(c);
    }

}
