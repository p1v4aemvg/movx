package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.event.Event;
import com.by.movx.event.ParentFilmClickedEvent;
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
public class ParentPanel extends LinkPanel<Film> {

    public ParentPanel(AnchorPane pane, Film film) {
        super(pane, film);
    }

    @Override
    protected List<Film> getItems() {

        List<Film> films = new ArrayList<>();

        Map<Integer, List<Film>> map = film.getChildren().stream()
                .sorted((f1, f2) -> {
                    int res = f1.getYear().compareTo(f2.getYear());
                    return res != 0 ? res :
                            f1.getId().compareTo(f2.getId());
                }).collect(Collectors.groupingBy(Film::getYear));
        SortedSet<Integer> keys = new TreeSet<Integer>(map.keySet());
        for (Integer key : keys) {
            films.addAll(map.get(key));
            films.add(null);
        }

        return films;
    }

    @Override
    protected List<Film> getParentItems() {
        return film.getParent() != null ? Lists.newArrayList(film.getParent()) : Lists.newArrayList();
    }

    @Override
    protected int rank(Film film) {
        return 2;
    }

    @Override
    protected String name(Film f) {
        return f != null ? (f.getYear() + " " + f.getName()) : "";
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








