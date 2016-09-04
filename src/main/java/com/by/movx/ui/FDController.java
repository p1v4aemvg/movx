package com.by.movx.ui;

import com.by.movx.entity.*;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class FDController {

    Film film;

    @FXML
    private TableView<Actor> actors;

    @FXML
    AnchorPane pane;

    @FXML
    AnchorPane paneL;

    @FXML
    Slider mark;

    @FXML
    Label markLabel;

    @FXML
    Label filmName;

    @Inject
    FilmRepository filmRepository;

    @Inject
    ActorRepository actorRepository;

    @Inject
    CountryRepository countryRepository;

    @FXML
    TextArea description;

    @FXML
    TextArea comment;

    @FXML
    Button save;

    @FXML
    Label fileName;

    @FXML
    TextField actor;

    @FXML
    Slider part;

    @FXML
    TextField partName;

    @FXML
    ColorPicker c1;

    @FXML
    ColorPicker c2;

    @FXML
    ColorPicker c3;

    @FXML
    ColorPicker c4;

    public void init() {

        List<Actor> dbData = ((List<Actor>) actorRepository.findAll()).stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());

        ObservableList<Actor> data = FXCollections.observableArrayList(dbData);

        TableColumn<Actor, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        actors.getColumns().setAll(nameColumn);
        actors.setItems(data);

        char chars[] = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЫЭЮЯ".toCharArray();
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
        comment.setText(film.getComment());
        description.setText(film.getDescription().getDescription() == null ? "" : film.getDescription().getDescription());
        markLabel.setText(film.getMark().toString());
        createLinks();

        c1.setValue(Color.valueOf(film.getC1()));
        c2.setValue(Color.valueOf(film.getC2()));
        c3.setValue(Color.valueOf(film.getC3()));
        c4.setValue(Color.valueOf(film.getC4()));
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
        film.setComment(comment.getText());
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

        film.getFas().add(new FilmActor(film, a, new Part((long) (int) part.getValue()), partName.getText()));

        Set<FilmActor> uq = film.getFas().stream()
                .collect(Collectors.groupingBy(FilmActor::uq))
                .entrySet().stream()
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toSet());

        film.setFas(uq);
        filmRepository.save(film);

        createLinks();
    }

    private void createLinks() {
        final List<FilmActor> fff = film.getFas().stream()
                .sorted((f1, f2) -> f1.getPart().getId().compareTo(f2.getPart().getId())).collect(Collectors.toList());
        List<Hyperlink> links = fff.stream()
                .map(fa -> {

                    Hyperlink l = new Hyperlink(fa.fullName());

                    switch (fa.getPart().getId().intValue()) {
                        case 1:
                            l.setTextFill(Paint.valueOf("#091a9c"));
                            break;
                        case 2:
                            l.setTextFill(Paint.valueOf("#0d69ff"));
                            break;
                        case 3:
                            l.setTextFill(Paint.valueOf("#898585"));
//                            l.setStyle("-fx-background-color: #898585;");
                            break;
                        default:
                            break;
                    }
                    return l;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < links.size(); i++) {
            links.get(i).setLayoutY(20 * i);
            links.get(i).setFont(new Font("Courier New", 12));

            final int i1 = i;
            links.get(i).setOnContextMenuRequested(event -> {
                TextField temp = new TextField();
                temp.setPrefHeight(15);
                temp.setLayoutY(i1 * 20);
                temp.setLayoutX(245);
                temp.setOnAction(event1 -> {
                    if (!temp.getText().isEmpty()) {
                        film.getFas().stream().filter(fa -> fa.equals(fff.get(i1))).findFirst().get().setPartName(temp.getText());
                        links.get(i1).setText(fff.get(i1).fullName());
                        filmRepository.save(film);
                    }
                    pane.getChildren().remove(temp);
                });
                pane.getChildren().add(temp);
            });
        }
        pane.getChildren().clear();
        pane.getChildren().addAll(links);
        pane.setPrefHeight(20 * links.size());
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

                boolean exist = film.getFas().stream().anyMatch(fd -> fd.getActor().getName().equals(a.getName()));

                if (!exist) {
                    film.getFas().add(new FilmActor(
                            film, a, new Part((long) (int) part.getValue()), partName.getText()));
                    filmRepository.save(film);
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

}
