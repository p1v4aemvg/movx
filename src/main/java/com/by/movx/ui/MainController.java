package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.ConfigurationControllers;
import com.by.movx.entity.*;
import com.by.movx.event.ActorClickedEvent;
import com.by.movx.event.FilmClickedEvent;
import com.by.movx.repository.*;
import com.by.movx.ui.common.TagAutoCompleteComboBoxListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class MainController {

    @Inject
    @Qualifier("fdView")
    ConfigurationControllers.View fdView;

    @Inject
    FDController fdController;

    @Inject
    @Qualifier("diagView")
    ConfigurationControllers.View diagView;

    @Inject
    DiagController diagController;

    @Inject
    @Qualifier("actorView")
    ConfigurationControllers.View actorView;

    @Inject
    ActorController actorController;

    @Inject
    @Qualifier("addView")
    ConfigurationControllers.View addView;

    @Inject
    AddController addController;

    @Inject
    private FilmRepository filmRepository;

    @Inject
    private CountryRepository countryRepository;

    @Inject
    private DirectorRepository directorRepository;

    @Inject
    private FilmActorRepository faRepository;

    @FXML
    Label count;

    @FXML
    private TableView<Film> table;

    @FXML
    private TextField txtYearFrom, txtYearTo;

    @FXML
    private ComboBox<Country> comboCountry;

    @FXML
    private ComboBox<Director> director;

    private ObservableList<Film> data;

    @FXML
    public ComboBox<Tag> tagCombo;

    @FXML
    HBox letterBox;

    @Inject
    FilmTagRepository filmTagRepository;

    @Inject
    TagRepository tagRepository;

    @FXML
    Button watchAll, noActors;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        Common.getInstance().getEventBus().register(this);

        count.setText(String.valueOf(filmRepository.count()));

        data = FXCollections.observableArrayList();
        ObservableList<Country> countries = FXCollections.observableArrayList((List<Country>) countryRepository.findAll());
        ObservableList<Director> directors = FXCollections.observableArrayList((List<Director>) directorRepository.findAll());

        TableColumn<Film, HBox> idColumn = new TableColumn<>("Country");
        idColumn.setCellValueFactory
                (c -> new SimpleObjectProperty<>(group(c)));

        TableColumn<Film, String> nameColumn = new TableColumn<>("Нахвание");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Film, Integer> markColumn = new TableColumn<>("Оценка");
        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));


        TableColumn<Film, String> enNameColumn = new TableColumn<>("Оригинальное название");
        enNameColumn.setCellValueFactory(new PropertyValueFactory<>("enName"));

        TableColumn<Film, Integer> yearColumn = new TableColumn<>("Год");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Film, String> folderColumn = new TableColumn<>("Папка");
        folderColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(folder(c)));

        table.getColumns().setAll(idColumn, markColumn, nameColumn, enNameColumn, yearColumn, folderColumn);
        table.setItems(data);

        comboCountry.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country object) {
                return object.getName();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        });
        comboCountry.setItems(countries);

        director.setConverter(new StringConverter<Director>() {
            @Override
            public String toString(Director object) {
                return object.getName();
            }

            @Override
            public Director fromString(String string) {
                return null;
            }
        });
        director.setItems(directors);

        tagCombo.setConverter(new StringConverter<Tag>() {
            @Override
            public String toString(Tag object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Tag fromString(String string) {
                return null;
            }
        });
        tagCombo.setItems(FXCollections.observableArrayList());
        new TagAutoCompleteComboBoxListener(tagCombo, tagRepository);

        fillLetters();
    }

    @FXML
    public void onRand() {
        Film f = filmRepository.findRandomFilm();
        data = FXCollections.observableArrayList(f);
        table.setItems(data);
    }

    @FXML
    public void rand10() {
        List<Film> films = filmRepository.findRandom10Film();
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void onTag () {
        Tag tag = tagCombo.getSelectionModel().getSelectedItem();
        if(tag == null) return;
        data = FXCollections.observableArrayList(filmRepository.getFilmsByTag(tag.getName()));
        table.setItems(data);
    }

    @FXML
    public void addCountry() {
        Film film = table.getSelectionModel().getSelectedItem();
        if (film == null) return;

        Country c = comboCountry.getSelectionModel().getSelectedItem();
        if (c == null) return;

        film.getCountries().add(c);
        filmRepository.save(film);
    }

    @FXML
    public void addTag() {
        Film film = table.getSelectionModel().getSelectedItem();
        if (film == null) return;

        Tag tag = tagCombo.getSelectionModel().getSelectedItem();
        if(tag == null) return;

        FilmTag ft = new FilmTag();
        ft.setFilm(film);
        ft.setTag(tag);

        filmTagRepository.save(ft);
    }

    @FXML
    public void edit() throws Exception {
        Film film = table.getSelectionModel().getSelectedItem();
        openFilm(film);
    }

    @FXML
    public void findByYear() {
        int from = Integer.valueOf(txtYearFrom.getText());
        int to = Integer.valueOf(txtYearTo.getText());

        List<Film> films = filmRepository.findByYearBetween(from, to);
        data = FXCollections.observableArrayList(films);

        table.setItems(data);
    }

    @FXML
    public void inc() {
        int from = Integer.valueOf(txtYearFrom.getText());
        txtYearFrom.setText(String.valueOf(from + 1));
        txtYearTo.setText(String.valueOf(from + 1));

        List<Film> films = filmRepository.findByYearBetween(from + 1, from + 1);
        data = FXCollections.observableArrayList(films);

        table.setItems(data);
    }

    @FXML
    public void dec() {
        int from = Integer.valueOf(txtYearFrom.getText());
        txtYearFrom.setText(String.valueOf(from - 1));
        txtYearTo.setText(String.valueOf(from - 1));

        List<Film> films = filmRepository.findByYearBetween(from - 1, from - 1);
        data = FXCollections.observableArrayList(films);

        table.setItems(data);
    }

    @FXML
    public void onActor() {
        actorController.init();

        if (actorView.getView().getScene() != null)
            actorView.getView().getScene().setRoot(new Button());

        Stage stage = new Stage();
        Scene scene = new Scene(actorView.getView());

        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void toWatch() {
        List<Film> films = filmRepository.findFirst10ByMarkOrderByIdDesc(0)
                .stream().sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList());
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void toWatchAll() {
        List<Film> films = filmRepository.findByMark(0);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
        watchAll.setText("WATCH " + films.size());
    }

    @FXML
    public void copyYearFrom() {
        String s = txtYearFrom.getText();
        txtYearTo.setText(s);
    }

    @FXML
    public void reload() {
        count.setText(String.valueOf(filmRepository.count()));
    }

    @FXML
    public void findByCountry() {
        Country c = comboCountry.getSelectionModel().getSelectedItem();
        List<Film> films = filmRepository.findByCountry(c);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void findByDirector() {
        Director d = director.getSelectionModel().getSelectedItem();

        List<Film> films = filmRepository.findByDirector(d);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void diag() throws Exception {
        startDiag();
    }

    @FXML
    public void setDirector() throws Exception {
        Director d = director.getSelectionModel().getSelectedItem();
        Film film = table.getSelectionModel().getSelectedItem();

        if (film != null && d != null) {
            film.setDirector(d);
            filmRepository.save(film);
        }
    }

    private void startDiag() throws Exception {
//        diagController.diagController(stats);
//        diagController.init();

        if (diagView.getView().getScene() != null)
            diagView.getView().getScene().setRoot(new Button());

        Stage stage = new Stage();
        stage.setScene(new Scene(diagView.getView()));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void add() {
        addController.init();

        if (addView.getView().getScene() != null)
            addView.getView().getScene().setRoot(new Button());

        Stage stage = new Stage();
        Scene scene = new Scene(addView.getView());

        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void noActors() {
        List<Film> films = filmRepository.findWithoutActors();
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
        noActors.setText("NO ACTORS " + films.size());
    }

    @FXML
    public void randTag() {
        Tag t = tagRepository.findRand();
        tagCombo.getSelectionModel().select(t);
    }

    private HBox group(TableColumn.CellDataFeatures<Film, HBox> c) {
        String cssBordering = "-fx-border-color:darkblue ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:7;\n"
                + "-fx-border-width:1.0";

        HBox g = new HBox();
        c.getValue().getCountries().stream()
                .map(k -> {
                            BorderPane p = new BorderPane();
                            Image im = new Image(new ByteArrayInputStream(k.getImage()));
                            ImageView iv = new ImageView(im);

                            p.setCenter(iv);
                            p.setMaxHeight(im.getHeight()+5);
                            p.setMaxWidth(im.getWidth()+5);

                            p.setStyle(cssBordering);
                            return p;
                        }
                )
                .forEach(p -> g.getChildren().add(p)
                );
        return g;
    }

    private String folder(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getType().getName();
    }

    private void fillLetters() {
        char chars[] = "0123456789АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЫЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        List<Hyperlink> letters = new ArrayList<>();

        int k = 0;
        for (final char c : chars) {
            Hyperlink h = new Hyperlink(String.valueOf(c));
            h.setLayoutX(8 * k);
            k++;
            h.setOnMouseClicked(event -> {
                data = FXCollections.observableArrayList(filmRepository.getFilmsBy1stLetter(String.valueOf(c)));
                table.setItems(data);});
            letters.add(h);
        }

        letterBox.getChildren().addAll(letters);
    }

    @Subscribe
    public void filmClicked(FilmClickedEvent e) {
        Film film = e.getData();
        openFilm(film);
    }

    @Subscribe
    public void actorClicked(ActorClickedEvent e) {
        List<Film> films = faRepository.findByActor(e.getData().getActor())
                .stream().map(FilmActor::getFilm).collect(Collectors.toList());
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    private void openFilm(Film film) {
        if (film == null) return;

        fdController.setFilm(film);
        fdController.init();

        if (fdView.getView().getScene() != null)
            fdView.getView().getScene().setRoot(new Button());

        Stage stage = new Stage();
        Scene scene = new Scene(fdView.getView());

        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
