package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmActor;
import com.by.movx.event.ActorClickedEvent;
import com.by.movx.event.Event;
import com.by.movx.repository.FilmActorRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class FilmActorsPanel extends LinkPanel<FilmActor> {

    private FilmActorRepository faRepository;

    public FilmActorsPanel(AnchorPane pane, Film film, FilmActorRepository faRepository) {
        super(pane, film);
        this.faRepository = faRepository;
    }

    protected int rank (FilmActor fa) {
        return fa.getPart().getId().intValue();
    }

    protected String name (FilmActor fa) {
        return fa.fullName();
    }

    @Override
    protected Event<FilmActor> onClicked(FilmActor fa) {
        return new ActorClickedEvent(fa);
    }

    @Override
    protected EventHandler<ContextMenuEvent> onParentRight(FilmActor fa) {
        return event -> {
            fa.setFilm(film);
            faRepository.save(fa);
            createLinks();
        };

    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(FilmActor fa, Hyperlink l) {
        return event -> {
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
        };
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(FilmActor fa) {
        return event -> {
            faRepository.delete(fa);
            createLinks();
        };
    }

    protected List<FilmActor> getItems(Film f) {
        return faRepository.findByFilm(f);
    }

}
