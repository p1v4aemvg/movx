package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.ConfigurationControllers;
import com.by.movx.common.Site;
import com.by.movx.entity.*;
import com.by.movx.event.*;
import com.by.movx.repository.*;
import com.by.movx.service.QueryEvaluator;
import com.by.movx.ui.common.FilmByNameAutoCompleteComboBoxListener;
import com.by.movx.ui.common.PTableColumn;
import com.by.movx.ui.common.TagAutoCompleteComboBoxListener;
import com.by.movx.ui.utils.UIUtils;
import com.by.movx.utils.CreatedDateCalculator;
import com.by.movx.utils.FilmUtils;
import com.by.movx.utils.URLFetcher;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class);

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
    private FilmActorRepository filmActorRepository;

    @Inject
    private FilmLangRepository filmLangRepository;

    @Inject
    private CountryRepository countryRepository;

    @Inject
    private ActorRepository actorRepository;

    @FXML
    Label count;

    @FXML
    private TableView<Film> table;

    @FXML
    private TextField txtYearFrom, txtYearTo;

    @FXML
    private ComboBox<Country> comboCountry;

    @FXML
    private ComboBox<Film.Type> comboType;

    @FXML
    private ComboBox<CustomQuery> customQuery;

    private ObservableList<Film> data;

    @FXML
    public ComboBox<Tag> tagCombo, cums;

    @FXML
    public ComboBox<FilmLang.Lang> lang;

    @FXML
    HBox letterBox;

    @FXML
    CheckBox sound, subtitled;

    @Inject
    FilmTagRepository filmTagRepository;

    @Inject
    TagRepository tagRepository;

    @Inject
    CustomQueryRepository customQueryRepository;

    @FXML
    Button query;

    @FXML
    ComboBox<Film> filmByNameCombo;

    @Inject
    QueryEvaluator queryEvaluator;

    @Inject
    URLFetcher fetcher;

    @FXML
    PTableColumn<Film, HBox> countryColumn;
    @FXML
    PTableColumn<Film, String> generalName, nameColumn, folderColumn, durationColumn;
    @FXML
    PTableColumn<Film, Integer> yearColumn, sizeColumn;
    @FXML
    PTableColumn<Film, Date> dateColumn;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        Common.getInstance().getEventBus().register(this);

        table.setRowFactory(tableView -> UIUtils.boldRow());

        count.setText(String.valueOf(filmRepository.countFilms()));

        data = FXCollections.observableArrayList();
        ObservableList<Country> countries = FXCollections.observableArrayList((List<Country>) countryRepository.findAll());

        createColumns();
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

        cums.setConverter(new StringConverter<Tag>() {
            @Override
            public String toString(Tag object) {
                return object.getName();
            }

            @Override
            public Tag fromString(String string) {
                return null;
            }
        });
        cums.setItems(FXCollections.observableArrayList(tagRepository.findCumulativeTags()));

        comboType.setConverter(new StringConverter<Film.Type>() {
            @Override
            public String toString(Film.Type object) {
                return object.getName();
            }

            @Override
            public Film.Type fromString(String string) {
                return null;
            }
        });
        comboType.setItems(FXCollections.observableArrayList(Film.Type.values()));

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

        filmByNameCombo.setConverter(new StringConverter<Film>() {
            @Override
            public String toString(Film object) {
                return object == null ? null :
                        object.getYear() + " " + FilmUtils.name(object, Film::getName);
            }

            @Override
            public Film fromString(String string) {
                return null;
            }
        });

        filmByNameCombo.setOnAction( e -> {
            Film f = filmByNameCombo.getSelectionModel().getSelectedItem();
            if( f != null) {
                List<Film> films = new ArrayList();
                films.add(f);
                if(!CollectionUtils.isEmpty(f.getChildren()) ) {
                    films.addAll(Lists.newArrayList(f.getChildren().stream()
                            .sorted((f1, f2) ->
                                    f1.getYear().equals(f2.getYear()) ?
                                            (f1.getId().compareTo(f2.getId())) :
                                            (f1.getYear().compareTo(f2.getYear())))
                            .collect(Collectors.toList())));
                }
                data = FXCollections.observableArrayList(films);
                table.setItems(data);
            }
        });

        filmByNameCombo.setItems(FXCollections.observableArrayList());
        new FilmByNameAutoCompleteComboBoxListener(filmByNameCombo, filmRepository);

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
        customQuery.setItems(FXCollections.observableArrayList(customQueryRepository.findByQueryType(CustomQuery.QueryType.FILM)));
        customQuery.setCellFactory(
                new Callback<ListView<CustomQuery>, ListCell<CustomQuery>>() {
                    @Override public ListCell<CustomQuery> call(ListView<CustomQuery> param) {
                        final ListCell<CustomQuery> cell = new ListCell<CustomQuery>() {
                            @Override public void updateItem(CustomQuery item,
                                                             boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item.getName());
                                    if (item.getCqType() == CustomQuery.CQType.URGENT) {
                                        setTextFill(Color.RED);
                                    } else if(item.getCqType() == CustomQuery.CQType.MEDIUM) {
                                        setTextFill(Color.ORANGE);
                                    } else if(item.getCqType() == CustomQuery.CQType.COLLECTION) {
                                        setTextFill(Color.DARKSLATEBLUE);
                                    } else if(item.getCqType() == CustomQuery.CQType.RAND) {
                                        setTextFill(Color.FORESTGREEN);
                                    } else {
                                        setTextFill(Color.BLACK);
                                    }
                                }
                                else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });

        lang.setConverter(new StringConverter<FilmLang.Lang>() {
            @Override
            public String toString(FilmLang.Lang object) {
                return object.name();
            }

            @Override
            public FilmLang.Lang fromString(String string) {
                return null;
            }
        });
        lang.setItems(FXCollections.observableArrayList(FilmLang.Lang.values()));

        fillLetters();
    }

    @FXML
    public void onRand() {
        Film f = filmRepository.findRandomFilm();
        data = FXCollections.observableArrayList(f);
        table.setItems(data);
    }

    @FXML
    public void onTag() {
        Tag tag = tagCombo.getSelectionModel().getSelectedItem();
        if (tag == null) return;
        data = FXCollections.observableArrayList(filmRepository.getFilmsByTag(tag.getName()));
        table.setItems(data);
    }

    @FXML
    public void addCountry() {
        Film film = firstOrSelected();
        if (film == null) return;

        Country c = comboCountry.getSelectionModel().getSelectedItem();
        if (c == null) return;

        film.getCountries().add(c);
        filmRepository.save(film);
    }

    @FXML
    public void addTag() {
        Film film = firstOrSelected();
        if (film == null) return;

        Tag tag = tagCombo.getSelectionModel().getSelectedItem();
        if (tag == null) return;

        FilmTag ft = new FilmTag();
        ft.setFilm(film);
        ft.setTag(tag);

        filmTagRepository.save(ft);
    }

    @FXML
    public void edit() throws Exception {
        Film film = firstOrSelected();
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
            actorView.getView().getScene().setRoot(anyButton());

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
    public void copyYearFrom() {
        String s = txtYearFrom.getText();
        txtYearTo.setText(s);
    }

    @FXML
    public void reload() {
        count.setText(String.valueOf(filmRepository.countFilms()));
    }

    @FXML
    public void findByCountry() {
        Country c = comboCountry.getSelectionModel().getSelectedItem();
        List<Film> films = filmRepository.findByCountry(c);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void findByType() {
        Film.Type type = comboType.getSelectionModel().getSelectedItem();
        if (type == null) return;
        List<Film> films = filmRepository.findByType(type);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void diag() throws Exception {
        startDiag();
    }

    private void startDiag() throws Exception {

        diagController.loadQ();

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

        stage.setOnCloseRequest(event -> {
            addController.setParent(null);
            System.out.println("xxx");
        });
    }

    @FXML
    public void randTag() {
        Tag t = tagRepository.findRand();
        tagCombo.getSelectionModel().select(t);
    }

    @FXML
    public void delete() {
        Film film = firstOrSelected();
        if(film == null) return;

        List<FilmActor> fas = filmActorRepository.findByFilm(film);
        if(!fas.isEmpty()) filmActorRepository.delete(fas);

        List<FilmTag> tags = filmTagRepository.findByFilm(film);
        if(!tags.isEmpty()) filmTagRepository.delete(tags);

        List<FilmLang> langs = filmLangRepository.findByFilm(film);
        if(!langs.isEmpty()) filmLangRepository.delete(langs);

        film.setParent(null);
        filmRepository.delete(film);
    }

    @FXML
    public void getCreatedAt() throws Exception {
        Film film = firstOrSelected();
        if (film == null) return;

        Long date = CreatedDateCalculator.getCreatedAt(film);
        if (date == null) return;
        film.setCreatedAt(new Timestamp(date));
        filmRepository.save(film);
    }

    @FXML
    public void withoutDate() {
        List<Film> films = filmRepository.filmsWithoutDate();
        for (Film f : films) {
            try {
            Long date = CreatedDateCalculator.getCreatedAt(f);
            if (date == null) continue;
            f.setCreatedAt(new Timestamp(date));
            filmRepository.save(f);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        data = FXCollections.observableArrayList(films);
        table.setItems(data);
    }

    @FXML
    public void google() {
        Film f = firstOrSelected();
        if (f == null) return;
        try {
            new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
                    fetcher.googleQ(FilmUtils.name(f, Film::getName) + " " + f.getYear())).start();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void explorer() throws Exception {
        Film f = firstOrSelected();
        if (f == null) return;
        File file = CreatedDateCalculator.getFile(f);
        if(file == null) return;
        LOG.info("explorer.exe /select,\"" + file + "\"");
        Runtime.getRuntime().exec("explorer.exe /select,\"" + file + "\"");
    }

    @FXML
    public void onFileSize() throws Exception {
        Film f = firstOrSelected();
        if (f == null) return;
        long size = CreatedDateCalculator.getFileSize(f);
        f.setFilmSize(size);
        filmRepository.save(f);
    }

    @FXML
    public void query() {
        CustomQuery q = customQuery.getSelectionModel().getSelectedItem();
        if(q == null) return;
        List<Film> films = queryEvaluator.getFilms(q);
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
        query.setText("◊◊◊ " + data.size() + " (" + queryEvaluator.getFilmsCount(q) + ")");
    }

    @FXML
    public void addLang() {
        Film f = firstOrSelected();
        if (f == null) return;
        FilmLang.Lang selectedL = lang.getSelectionModel().getSelectedItem();
        if(selectedL == null) return;
        Boolean isSound = sound.isSelected();
        Boolean isSub = subtitled.isSelected();

        FilmLang fl = filmLangRepository.findTop1ByFilmAndLang(f, selectedL);
        if(fl == null) {
            fl = new FilmLang();
            fl.setFilm(f);
            fl.setLang(selectedL);
        }

        fl.setSound(isSound);
        fl.setSubtitled(isSub);
        filmLangRepository.save(fl);
    }

    @FXML
    public void onW() throws Exception {
        fetchSite(Site.WIKI);
    }

    @FXML
    public void onKP() throws Exception {
        fetchSite(Site.KP);
    }

    @FXML
    public void onKT() throws Exception {
        fetchSite(Site.KINO_TEATR);
    }

    @FXML
    public void onIVI() throws Exception {
        fetchSite(Site.IVI);
    }

    @FXML
    public void on1() {
        Film film = firstOrSelected();
        if(film == null) return;

        film.setNeverDelete(true);
        filmRepository.save(film);
    }

    @FXML
    public void on0() {
        Film film = firstOrSelected();
        if(film == null) return;

        film.setNeverDelete(false);
        filmRepository.save(film);
    }

    @FXML
    public void onCum() {
        Tag tag = cums.getSelectionModel().getSelectedItem();
        if(tag == null) return;

        List<Film> films = filmRepository.findByCumulativeTag(tag.getId());
        data = FXCollections.observableArrayList(films);
        table.setItems(data);
        query.setText("QUERY " + data.size());

        tagCombo.getSelectionModel().select(tag);
    }

    private void fetchSite(Site site) throws Exception {
        Film f = firstOrSelected();
        if(f == null) return;

        String url = fetcher.googleQ(FilmUtils.name(f, Film::getName) + " " + f.getYear() + site.getAddSearch());
        String link = fetcher.fetch(url, site.getContain());
        if(link == null) return;

        f.getDescription().setExternalLink(link);
        filmRepository.save(f);

        new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", link).start();
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
                table.setItems(data);
            });
            letters.add(h);
        }

        letterBox.getChildren().clear();
        letterBox.getChildren().addAll(letters);
    }

    @Subscribe
    public void filmClicked(FilmClickedEvent e) {
        Film film = e.getData().getFilm();
        openFilm(film);
    }

    @Subscribe
    public void actorClicked(ActorClickedEvent e) {
        actorController.setCurrent(e.getData().getActor());
        onActor();
    }

    @Subscribe
    public void statActorClicked(StatActorClickedEvent e) {
        Actor a = actorRepository.findOne((Long)(e.getData()[2]));
        actorController.setCurrent(a);
        onActor();
    }

    @Subscribe
    public void tagClicked(TagClickedEvent e) {
        Tag t = e.getData().getTag();
        tagCombo.getSelectionModel().select(t);
        onTag();
    }

    @Subscribe
    public void reloadFD(ReloadParentEvent e) {
        Film f = filmRepository.findOne(e.getData());
        fdController.setFilm(f);
        fdController.init();
    }

    @Subscribe
    public void subFilmAdded(AddSubFilmEvent e) {
        addController.setParent(e.getData());
        add();
    }

    private void openFilm(Film film) {
        if (film == null) return;

        fdController.setFilm(film);
        fdController.init();

        if (fdView.getView().getScene() != null)
            fdView.getView().getScene().setRoot(anyButton());

        Stage stage = new Stage();
        Scene scene = new Scene(fdView.getView());

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

    private Film firstOrSelected() {
        Film film = null;
        if (table.getItems().size() == 1) {
            film = table.getItems().get(0);
        } else if (table.getItems().size() > 1) {
            film = table.getSelectionModel().getSelectedItem();
        }
        return film;
    }

    @FXML
    public void reloadAll() {
        init();
    }

    private void createColumns() {
        countryColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(UIUtils.wrapCountries(c)));
        generalName.setCellValueFactory(c -> new SimpleObjectProperty<>(parent(c)));
        nameColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(name(c)));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        folderColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(folder(c)));
        durationColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(duration(c)));
        sizeColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(size(c)));
    }

    private static String name(TableColumn.CellDataFeatures<Film, String> c) {
        return FilmUtils.name(c.getValue(), Film::getName);
    }

    private static String folder(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getType().getName();
    }

    private static String duration(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getDuration().getName();
    }

    private static String parent(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getParent() == null ? "" : FilmUtils.name(c.getValue().getParent(), Film::getName);
    }

    private static Integer size(TableColumn.CellDataFeatures<Film, Integer> c) {
        return c.getValue().getFilmSize() == null ? 0 : Long.valueOf(c.getValue().getFilmSize()/(1024*1024)).intValue();
    }
}
