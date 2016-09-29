package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.entity.*;
import com.by.movx.event.FilmClickedEvent;
import com.by.movx.repository.ActorRepository;
import com.by.movx.repository.FilmActorRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class ActorController {

    @FXML
    private TableView<Actor> actors;

    @FXML
    AnchorPane pane;

    @FXML
    AnchorPane paneL;

    @FXML
    Label markLabel;

    @FXML
    Label filmName;

    @Inject
    FilmActorRepository filmActorRepository;

    @Inject
    ActorRepository actorRepository;

    @FXML
    TextField actor;

    @FXML
    Slider part;

    @FXML
    TextField partName;

    @FXML
    ImageView img;

    public void init() {
        actors.setRowFactory(new Callback<TableView<Actor>, TableRow<Actor>>() {
            @Override
            public TableRow<Actor> call(TableView<Actor> tableView) {
                final TableRow<Actor> row = new TableRow<Actor>() {
                    @Override
                    protected void updateItem(Actor actor, boolean empty) {
                        super.updateItem(actor, empty);
                    }
                };
                row.setOnMouseClicked(event -> {
                    Actor a = actors.getSelectionModel().getSelectedItem();
                    createLinks(a);
                    setImg(a);
                });
                return row;
            }
        });

        List<Actor> dbData = ((List<Actor>) actorRepository.findAll()).stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());

        ObservableList<Actor> data = FXCollections.observableArrayList(dbData);

        TableColumn<Actor, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

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
    }

    private void createLinks(Actor a) {
        final List<FilmActor> fff = filmActorRepository.findByActor(a);
        List<Hyperlink> links = fff.stream()
                .map(fa -> {
                    Hyperlink l = new Hyperlink(fa.film());
                    l.setId(fa.getId().toString());
                    switch (fa.getPart().getId().intValue()) {
                        case 1:
                            l.setTextFill(Paint.valueOf("#091a9c"));
                            break;
                        case 2:
                            l.setTextFill(Paint.valueOf("#0d69ff"));
                            break;
                        case 3:
                            l.setTextFill(Paint.valueOf("#898585"));
                            break;
                        default:break;
                    }
                    l.setOnAction(event -> {
                        Common.getInstance().getEventBus().post(new FilmClickedEvent(fa.getFilm()));
                    });

                    return l;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < links.size(); i++) {
            links.get(i).setLayoutY(20 * i);
            links.get(i).setFont(new Font("Courier New", 12));

            final int i1 = i;
            links.get(i).setOnContextMenuRequested(event -> {
                Hyperlink l = (Hyperlink) event.getSource();
                TextField temp = new TextField();
                temp.setId(l.getId());
                temp.setPrefHeight(15);
                temp.setLayoutY(i1 * 20);
                temp.setLayoutX(345);
                temp.setOnAction(event1 -> {
                    if (!temp.getText().isEmpty()) {
                        FilmActor fa = filmActorRepository.findOne(Long.valueOf(((TextField) event1.getSource()).getId()));
                        fa.setPartName(temp.getText());
                        ((Hyperlink)event.getSource()).setText(fa.film());
                        filmActorRepository.save(fa);
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

    private void setImg(Actor a) {
        if(a.getImg() == null) return;
        img.setImage(new Image(new ByteArrayInputStream(a.getImg())));
    }

}
