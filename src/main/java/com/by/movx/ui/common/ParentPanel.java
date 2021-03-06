package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.event.Event;
import com.by.movx.event.ParentFilmClickedEvent;
import com.by.movx.utils.FilmUtils;
import com.google.common.collect.Lists;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mobx
 * on 30.09.2017.
 */
public class ParentPanel extends FilmTargetLinkPanel<Film> {

    public ParentPanel(AnchorPane pane, Film film) {
        super(pane, film);
    }

    @Override
    protected List<Film> getItems(Film f) {
        return Lists.newArrayList(f.getChildren().stream()
                .sorted((f1, f2) ->
                        f1.getYear().equals(f2.getYear()) ? (f1.getId().compareTo(f2.getId())) : (f1.getYear().compareTo(f2.getYear())))
                .collect(Collectors.toList()));
    }

    @Override
    protected List<Film> getParentItems() {
        return target.getParent() != null ? Lists.newArrayList(target.getParent()) : Lists.newArrayList();
    }

    @Override
    protected int rank(Film film) {
        return 2;
    }

    @Override
    protected String name(Film f) {
        return f.getYear() + " " + FilmUtils.name(f, Film::getName);
    }

    @Override
    protected Event<Film> onClicked(Film f) {
        return new ParentFilmClickedEvent(f);
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onParentRight(Film film) {
        return event -> {};
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(Film film, Hyperlink l) {
        return event -> {};
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(Film film) {
        return event -> {};
    }
}








