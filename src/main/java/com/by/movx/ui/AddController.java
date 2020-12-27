package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.entity.Country;
import com.by.movx.entity.Film;
import com.by.movx.entity.FilmDescription;
import com.by.movx.event.ReloadParentEvent;
import com.by.movx.repository.CountryRepository;
import com.by.movx.repository.FilmRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private HBox cBox;

    @FXML
    private CheckBox isEntity;

    private Set<Country> selectedCountries = new HashSet<>();

    public void init() {
        film = new Film();
        film.setCountries(new HashSet<>());
        name.clear();
        enname.clear();
        year.clear();
        selectedCountries.clear();
        reloadCountries();

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
        if (parent != null) {
            film.setType(parent.getType());
            type.setValue(parent.getType());

            film.setDuration(parent.getDuration());
            duration.setValue((double) parent.getDuration().ordinal());

            selectedCountries.addAll(parent.getCountries());
            reloadCountries();
        }
    }

    @FXML
    public void add() throws Exception {
        film.setName(name.getText());
        film.setEnName(enname.getText().isEmpty() ? null : enname.getText());
        film.setYear(Integer.valueOf(year.getText()));
        film.setType(type.getSelectionModel().getSelectedItem());
        film.setEntity(isEntity.isSelected());
        film.setMark(film.getEntity() ? 0 : -1);
        film.setDuration(Film.Duration.of((int) duration.getValue()));
        film.setParent(parent);
        film.setCountries(selectedCountries);
        film = filmRepository.save(film);
        film.setDescription(new FilmDescription(film));
        filmRepository.save(film);

        if (parent != null) {
            Common.getInstance().getEventBus().post(new ReloadParentEvent(parent.getId()));
            parent = null;
        }
    }

    @FXML
    public void countryAdded() {
        Country c = country.getSelectionModel().getSelectedItem();
        if (c != null) selectedCountries.add(c);
        reloadCountries();
    }

    public void setParent(Film parent) {
        this.parent = parent;
    }

    private void reloadCountries() {
        cBox.getChildren().clear();
        String cssBordering = "-fx-border-color:darkblue ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:7;\n"
                + "-fx-border-width:1.0";

        selectedCountries.stream()
                .map(k -> {
                            BorderPane p = new BorderPane();
                            Image im = new Image(new ByteArrayInputStream(k.getImage()));
                            ImageView iv = new ImageView(im);

                            iv.setOnMouseClicked(event -> {
                                selectedCountries.remove(k);
                                reloadCountries();
                            });

                            p.setCenter(iv);
                            p.setMaxHeight(im.getHeight() + 5);
                            p.setMaxWidth(im.getWidth() + 5);

                            p.setStyle(cssBordering);
                            return p;
                        }
                ).forEach(p -> cBox.getChildren().add(p));
    }
}
