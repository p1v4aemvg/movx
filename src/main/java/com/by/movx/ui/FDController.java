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
import com.by.movx.utils.CreatedDateCalculator;
import com.by.movx.utils.FilmUtils;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 10.02.2016.
 */

public class FDController {
    private static final String  URL_REGEXP = "\\(?\\b(https?://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
    private static final Pattern pattern = Pattern.compile(URL_REGEXP);

    Film film;

    @FXML
    private TableView<Actor> actors;

    @FXML
    AnchorPane pane, paneL, mainPane, parentPanel, tagPanel, langPanel, extLinksPanel;

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
        filmName.setText(StringUtils.abbreviateMiddle(FilmUtils.name(film, Film::getName) + " " + film.getYear(), ".", 45));

        mark.setValue(film.getMark());
        markLabel.setText(film.getMark().toString());

        new FilmActorsPanel(pane, film, faRepository).createLinks();
        new ParentPanel(parentPanel, film).createLinks();
        new TagsPanel(tagPanel, film, ftRepository).createLinks();
        new FilmLangPanel(langPanel, film, flRepository).createLinks();

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
    public void onExternalLink() {
        String url = getLink(film);
        if (url == null) return;
        openUrl(url);
    }

    private void openUrl(String url) {
        try {
            new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", url).start();
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void explorer() throws Exception {
        if (film == null) return;
        File file = CreatedDateCalculator.getFile(film);
        if(file == null) return;
        Runtime.getRuntime().exec("explorer.exe /select,\"" + file + "\"");
    }

    private void autoSaveText() {
        String filmExtLink = getLink(film);
        if(filmExtLink == null) {
            extHyperLink.setTextFill(Paint.valueOf("#ff0000"));
        } else {
            extHyperLink.setTextFill(Paint.valueOf("#038b47"));
            extHyperLink.setText(StringUtils.abbreviate(filmExtLink, 60));
        }

        extLink.clear();
        extLink.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)) return;
            if(!eq(getLink(film), newValue)) {
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
            if(newValue == null) return;
            if(!eq(film.getDescription().getDescription(), newValue)) {
                film.getDescription().setDescription(newValue);
                filmRepository.save(film);
            }
        });

        extractLinksFromDescription();

        mark.valueProperty().addListener((obs, oldV, newV) -> {
            if(!eq(film.getMark(), newV.intValue())) {
                film.setMark(newV.intValue());
                filmRepository.save(film);
            }
        });

    }

    private void extractLinksFromDescription() {
        extLinksPanel.getChildren().clear();

        String text = StringUtils.defaultString(film.getDescription().getDescription());
        Matcher matcher = pattern.matcher(text);
        int i = 0;
        while (matcher.find()) {
            String url = matcher.group();
            Hyperlink l = new Hyperlink(StringUtils.abbreviate(url, 60));
            l.setLayoutY(i * 18);
            l.setOnMouseClicked(event -> {
                openUrl(url);
            });
            extLinksPanel.getChildren().add(l);
            i++;
        }

        description.setText(text);
    }

    private String getLink (Film f) {
        String link = f.getDescription().getExternalLink();
        while (link == null && f.getParent() != null) {
            f = f.getParent();
            link = f.getDescription().getExternalLink();
        }
        return link;
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
        this.film = e.getData();
        init();
    }
}
