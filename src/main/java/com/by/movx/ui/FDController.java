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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
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
    TextArea description, comment;

    @FXML
    Button save;

    @FXML
    TextField actor, partName;

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

        c1.setValue(Color.valueOf(film.getC1()));
        c2.setValue(Color.valueOf(film.getC2()));
        c3.setValue(Color.valueOf(film.getC3()));
        c4.setValue(Color.valueOf(film.getC4()));

        mainPane.setStyle(generateStyleStr(film));
    }

    @FXML
    public void edit() {
        description.setEditable(!description.isEditable());
        save.setDisable(!save.isDisable());
        comment.setEditable(!comment.isEditable());
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

                    return l;
                }))
                .collect(Collectors.toList());

        for (int i = 0; i < links.size(); i++) {
            links.get(i).setLayoutY(20 * i);
            links.get(i).setFont(new Font("Courier New", 12));
        }

        pane.getChildren().clear();
        pane.getChildren().addAll(links);
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
        if(film.getParent()!=null) {
            Hyperlink l = createParent(i, film.getParent());
            parentPanel.getChildren().add(l);
            i++;
        }
        if(!CollectionUtils.isEmpty(film.getChildren())) {
            List<Film> children = film.getChildren().stream()
                    .sorted((f1, f2) -> {
                        int res = f1.getYear().compareTo(f2.getYear());
                        return res != 0 ? res :
                                f1.getId().compareTo(f2.getId());
                    }).collect(Collectors.toList());

            for(Film f : children) {
                Hyperlink l = createParent(i, f);
                parentPanel.getChildren().add(l);
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

    private void createTags() {
        tagPanel.getChildren().clear();
        List<FilmTag> tags = ftRepository.findByFilm(film);
        int i = 0;
        if(!CollectionUtils.isEmpty(tags)) {
            for(FilmTag ft : tags ) {
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
        film.setC1(c1.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC2() {
        film.setC2(c2.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC3() {
        film.setC3(c3.getValue().toString());
        filmRepository.save(film);
    }

    @FXML
    public void onC4() {
        film.setC4(c4.getValue().toString());
        filmRepository.save(film);
    }

    private String generateStyleStr(Film f) {
        return "-fx-background-color: linear-gradient( to bottom right,  "
                + f.getC1().replace("0x", "#") + "  , "
                + f.getC2().replace("0x", "#") + "  , "
                + f.getC3().replace("0x", "#") + " , "
                + f.getC4().replace("0x", "#") + " )";
    }

}
