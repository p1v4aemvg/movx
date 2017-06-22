package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.entity.*;
import com.by.movx.event.ActorClickedEvent;
import com.by.movx.event.AddSubFilmEvent;
import com.by.movx.event.TagClickedEvent;
import com.by.movx.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class FDController {

    Film film;

    @FXML
    private TableView<Actor> actors;

    @FXML
    AnchorPane pane, paneL, mainPane, parentPanel, tagPanel;

    @FXML
    Slider mark, part;

    @FXML
    Label markLabel, filmName, fileName;

    @Inject
    FilmRepository filmRepository;

    @Inject
    ActorRepository actorRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    FilmActorRepository faRepository;

    @Inject
    FilmTagRepository ftRepository;

    @FXML
    TextArea description;

    @FXML
    Button save;

    @FXML
    TextField actor, partName, extLink;

    @FXML
    ColorPicker c1, c2, c3, c4;


    public void init() {

        List<Actor> dbData = ((List<Actor>) actorRepository.findAll()).stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());

        ObservableList<Actor> data = FXCollections.observableArrayList(dbData);

        TableColumn<Actor, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        actors.getColumns().setAll(nameColumn);
        actors.setItems(data);

        char chars[] = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЭЮЯ".toCharArray();
        List<Hyperlink> letters = new ArrayList<>();

        int k = 0;
        for (final char c : chars) {
            Hyperlink h = new Hyperlink(String.valueOf(c));
            h.setLayoutY(18 * k);
            k++;
            h.setOnMouseClicked(event -> actors.setItems(FXCollections.observableArrayList(
                    dbData.stream().filter(a -> a.getName().codePointAt(0) == c)
                            .collect(Collectors.toList()))));
            letters.add(h);
        }

        paneL.getChildren().addAll(letters);

        filmName.setWrapText(true);
        filmName.setText(film.getName() + " " + film.getYear());

        mark.setValue(film.getMark());
        description.setText(film.getDescription().getDescription() == null ? "" : film.getDescription().getDescription());
        markLabel.setText(film.getMark().toString());
        createLinks();
        createParents();
        createTags();

        FilmColor color = film.getColor();
        if (color != null) {
            c1.setValue(Color.valueOf(color.getC1()));
            c2.setValue(Color.valueOf(color.getC2()));
            c3.setValue(Color.valueOf(color.getC3()));
            c4.setValue(Color.valueOf(color.getC4()));
        }

        mainPane.setStyle(generateStyleStr(film));

        autoSaveText();
    }

    @FXML
    public void save() throws Exception {
        film.getDescription().setDescription(description.getText());
        film.setMark((int) mark.getValue());
        filmRepository.save(film);
    }

    @FXML
    @Transactional
    public void add() throws Exception {
        Actor a = actorRepository.findByName(actor.getText());
        if (a == null) {
            a = new Actor(actor.getText());
            a = actorRepository.save(a);
        }
        faRepository.save(new FilmActor(film, a, new Part((long) (int) part.getValue()), partName.getText()));
        createLinks();
    }

    private void createLinks() {
        final List<FilmActor> fff = faRepository.findByFilm(film);
        final List<FilmActor> parentFFF = film.getParent() == null ? new ArrayList() : faRepository.findByFilm(film.getParent());

        List<Hyperlink> removed = new ArrayList<>();
        List<Hyperlink> links = Stream.concat(
                parentFFF.stream().map(fa -> {

                    Hyperlink l = createLnk(fa, "#8b0201", "#8b4834", "#8b6c2b");
                    l.setOnAction(event -> {
                        Common.getInstance().getEventBus().post(new ActorClickedEvent(fa));
                    });
                    l.setOnContextMenuRequested(event -> {
                        fa.setFilm(film);
                        faRepository.save(fa);
                        createLinks();
                    });

                    Hyperlink removeLink = new Hyperlink("-");
                    removed.add(removeLink);

                    return l;
                }),
                fff.stream().map(fa -> {


                    Hyperlink l = createLnk(fa, "#091a9c", "#0d69ff", "#898585");
                    l.setOnAction(event -> {
                        Common.getInstance().getEventBus().post(new ActorClickedEvent(fa));
                    });
                    l.setOnContextMenuRequested(event -> {
                        TextField temp = new TextField();
                        temp.setPrefHeight(15);
                        temp.setLayoutY(l.getLayoutY());
                        temp.setLayoutX(245);
                        temp.setOnAction(event1 -> {
                            if (!temp.getText().isEmpty()) {
                                fa.setPartName(temp.getText());
                                l.setText(fa.fullName());
                                faRepository.save(fa);
                            }
                            pane.getChildren().remove(temp);
                        });
                        pane.getChildren().add(temp);
                    });

                    Hyperlink removeLink = new Hyperlink("╳");
                    removeLink.setOnAction(event -> {
                        faRepository.delete(fa);
                        createLinks();
                    });
                    removed.add(removeLink);

                    return l;
                }))
                .collect(Collectors.toList());

        for (int i = 0; i < links.size(); i++) {
            links.get(i).setLayoutY(20 * i);
            links.get(i).setLayoutX(12);
            links.get(i).setFont(new Font("Courier New", 12));
        }

        for (int i = 0; i < removed.size(); i++) {
            removed.get(i).setLayoutY(20 * i);
            removed.get(i).setFont(new Font("Courier New", 12));
        }

        pane.getChildren().clear();
        pane.getChildren().addAll(links);
        pane.getChildren().addAll(removed);
        pane.setPrefHeight(20 * links.size());
    }

    private Hyperlink createLnk(FilmActor fa, String color1, String color2, String color3) {
        Hyperlink l = new Hyperlink(fa.fullName());

        switch (fa.getPart().getId().intValue()) {
            case 1:
                l.setTextFill(Paint.valueOf(color1));
                break;
            case 2:
                l.setTextFill(Paint.valueOf(color2));
                break;
            case 3:
                l.setTextFill(Paint.valueOf(color3));
                break;
            default:
                break;
        }
        return l;
    }

    private void createParents() {
        parentPanel.getChildren().clear();
        int i = 0;
        if (film.getParent() != null) {
            Hyperlink l = createParent(i, film.getParent());
            parentPanel.getChildren().add(l);
            i++;
        }
        if (!CollectionUtils.isEmpty(film.getChildren())) {
            List<Film> children = film.getChildren().stream()
                    .sorted((f1, f2) -> {
                        int res = f1.getYear().compareTo(f2.getYear());
                        return res != 0 ? res :
                                f1.getId().compareTo(f2.getId());
                    }).collect(Collectors.toList());

            Map<Integer, List<Film>> map = children.stream().collect(Collectors.groupingBy(Film::getYear));
            SortedSet<Integer> keys = new TreeSet<Integer>(map.keySet());
            for (Integer key : keys) {
                for (Film f : map.get(key)) {
                    Hyperlink l = createParent(i, f);
                    parentPanel.getChildren().add(l);
                    i++;
                }
                createEmptyLink(i);
                i++;
            }
        }

        parentPanel.setPrefHeight(20 * i);
    }

    private Hyperlink createParent(int i, Film f) {
        Hyperlink l = new Hyperlink(f.getYear() + " " + f.getName());
        l.setLayoutY(20 * i);
        l.setFont(new Font("Courier New", 12));

        l.setOnAction(event -> {
            this.film = f;
            init();
        });

        return l;
    }

    private Hyperlink createEmptyLink(int i) {
        Hyperlink l = new Hyperlink("-------------------------------------");
        l.setLayoutY(20 * i);
        l.setFont(new Font("Courier New", 12));

        return l;
    }

    private void createTags() {
        tagPanel.getChildren().clear();

        int i = 0;
        if (film.getParent() != null) {
            List<FilmTag> parentTags = ftRepository.findByFilm(film.getParent());
            if (!CollectionUtils.isEmpty(parentTags)) {
                for (FilmTag ft : parentTags) {
                    Hyperlink l = createTagLink(i, ft);
                    tagPanel.getChildren().add(l);
                    i++;
                }
            }
        }
        List<FilmTag> tags = ftRepository.findByFilm(film);
        if (!CollectionUtils.isEmpty(tags)) {
            for (FilmTag ft : tags) {
                Hyperlink l = createTagLink(i, ft);
                tagPanel.getChildren().add(l);
                i++;
            }
        }
        tagPanel.setPrefHeight(20 * i);
    }

    private Hyperlink createTagLink(int i, FilmTag ft) {
        Hyperlink l = new Hyperlink(ft.getTag().getName());
        l.setLayoutY(20 * i);
        l.setFont(new Font("Courier New", 12));

        l.setOnAction(event -> {
            Common.getInstance().getEventBus().post(new TagClickedEvent(ft));

        });

        return l;
    }

    @FXML
    private void onDragDetected(MouseEvent event) { //drag
        Actor selected = actors.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dragboard db = actors.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selected.getName());
            db.setContent(content);
            event.consume();
        }
    }

    @FXML
    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    private void onDragDropped(DragEvent event) {

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (event.getDragboard().hasString()) {

            Actor a = actors.getSelectionModel().getSelectedItem();
            if (a != null) {

                boolean exist = faRepository.findTop1ByActorAndFilm(a, film) != null;

                if (!exist) {
                    faRepository.save(new FilmActor(
                            film, a, new Part((long) (int) part.getValue()), partName.getText()));
                    createLinks();
                }
            }

            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @FXML
    public void sub() {
        Common.getInstance().getEventBus().post(new AddSubFilmEvent(film));
    }

    @FXML
    public void onC1() {
        FilmColor color = film.getColor();
        if (color == null) {
            color = new FilmColor(film);
            film.setColor(color);
        }
        color.setC1(c1.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC2() {
        FilmColor color = film.getColor();
        if (color == null) {
            color = new FilmColor(film);
            film.setColor(color);
        }
        color.setC2(c2.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC3() {
        FilmColor color = film.getColor();
        if (color == null) {
            color = new FilmColor(film);
            film.setColor(color);
        }
        color.setC3(c3.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC4() {
        FilmColor color = film.getColor();
        if (color == null) {
            color = new FilmColor(film);
            film.setColor(color);
        }
        color.setC4(c4.getValue().toString());
        filmRepository.save(film);
    }

    private String generateStyleStr(Film f) {
        FilmColor color = f.getColor() != null ? f.getColor() : new FilmColor();
        return "-fx-background-color: linear-gradient( to bottom right,  "
                + color.getC1().replace("0x", "#") + "  , "
                + color.getC2().replace("0x", "#") + "  , "
                + color.getC3().replace("0x", "#") + " , "
                + color.getC4().replace("0x", "#") + " )";
    }

    @FXML
    public void onExternalLink() {
        String url = film.getDescription().getExternalLink();
        if (url == null) return;
        try {
            new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", url).start();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private void autoSaveText() {
        extLink.clear();
        extLink.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!eq(film.getDescription().getExternalLink(), newValue)) {
                film.getDescription().setExternalLink(newValue);
                filmRepository.save(film);
            }
        });

        description.setEditable(true);
        description.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!eq(film.getDescription().getDescription(), newValue)) {
                film.getDescription().setDescription(newValue);
                filmRepository.save(film);
            }
        });

        mark.blockIncrementProperty().addListener((obs, oldV, newV) -> {
            if(!eq(film.getMark(), newV.intValue())) {
                film.setMark(newV.intValue());
                filmRepository.save(film);
            }
        });

    }

    private boolean eq(String s1, String s2) {
        if(s1 == null) return s2 == null;
        return s2 != null && s1.equals(s2);
    }

    private boolean eq(Integer s1, Integer s2) {
        if(s1 == null) return s2 == null;
        return s2 != null && s1.equals(s2);
    }

}
