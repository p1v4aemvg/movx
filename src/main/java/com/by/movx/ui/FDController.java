package com.by.movx.ui;

import com.by.movx.Common;
import com.by.movx.entity.*;
import com.by.movx.event.AddSubFilmEvent;
import com.by.movx.event.ParentFilmClickedEvent;
import com.by.movx.repository.*;
import com.by.movx.ui.common.FilmActorsPanel;
import com.by.movx.ui.common.FilmLangPanel;
import com.by.movx.ui.common.ParentPanel;
import com.by.movx.ui.common.TagsPanel;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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
    AnchorPane pane, paneL, mainPane, parentPanel, tagPanel, langPanel;

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

    @Inject
    FilmLangRepository flRepository;

    @FXML
    TextArea description;

    @FXML
    TextField actor, partName, extLink;

    @FXML
    ColorPicker c1, c2, c3, c4;

    @FXML
    Hyperlink extHyperLink;

    public void init() {

        Common.getInstance().getEventBus().register(this);

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

        new FilmActorsPanel(pane, film, faRepository).createLinks();
        new ParentPanel(parentPanel, film).createLinks();
        new TagsPanel(tagPanel, film, ftRepository).createLinks();
        new FilmLangPanel(langPanel, film, flRepository).createLinks();

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
    @Transactional
    public void add() throws Exception {
        Actor a = actorRepository.findByName(actor.getText());
        if (a == null) {
            a = new Actor(actor.getText());
            a = actorRepository.save(a);
        }
        faRepository.save(new FilmActor(film, a, new Part((long) (int) part.getValue()), partName.getText()));
        new FilmActorsPanel(pane, film, faRepository).createLinks();
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
                    new FilmActorsPanel(pane, film, faRepository).createLinks();
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
        setColor(c1, FilmColor::setC1);
    }

    @FXML
    public void onC2() {
        setColor(c2, FilmColor::setC2);
    }

    @FXML
    public void onC3() {
        setColor(c3, FilmColor::setC3);
    }

    @FXML
    public void onC4() {
        setColor(c4, FilmColor::setC4);
    }

    private void setColor(ColorPicker cp, BiConsumer<FilmColor, String> f) {
        FilmColor color = film.getColor();
        if (color == null) {
            color = new FilmColor(film);
            film.setColor(color);
        }
        f.accept(color, cp.getValue().toString());
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
        if(film.getDescription().getExternalLink() == null) {
            extHyperLink.setTextFill(Paint.valueOf("#ff0000"));
        } else {
            extHyperLink.setTextFill(Paint.valueOf("#038b47"));
        }
        extLink.clear();
        extLink.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)) return;
            if(!eq(film.getDescription().getExternalLink(), newValue)) {
                try {
                    String decoded = URLDecoder.decode(newValue, "UTF-8");
                    film.getDescription().setExternalLink(decoded);
                    filmRepository.save(film);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        description.setEditable(true);
        description.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)) return;
            if(!eq(film.getDescription().getDescription(), newValue)) {
                film.getDescription().setDescription(newValue);
                filmRepository.save(film);
            }
        });

        mark.valueProperty().addListener((obs, oldV, newV) -> {
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

    @Subscribe
    public void parentClicked(ParentFilmClickedEvent e) {
        if(e.getData() != null) {
            this.film = e.getData();
            init();
        }
    }
}
