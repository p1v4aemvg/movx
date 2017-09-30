package com.by.movx.ui.common;

import com.by.movx.Common;
import com.by.movx.entity.Film;
import com.by.movx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by movx
 * on 30.09.2017.
 */
public abstract class LinkPanel<T> {

    protected AnchorPane pane;
    protected Film film;

    public LinkPanel(AnchorPane pane, Film film) {
        this.pane = pane;
        this.film = film;
    }

    protected abstract List<T> getItems();  // Stream<List<T>>
    protected abstract List<T> getParentItems();

    protected abstract int rank(T t);
    protected abstract String name(T t);

    protected abstract Event<T> onClicked(T t);
    protected abstract EventHandler<? super ContextMenuEvent> onParentRight(T t);
    protected abstract EventHandler<? super ContextMenuEvent> onItemRight(T t, Hyperlink l);
    protected abstract EventHandler<ActionEvent> onRemove(T t);

    public void createLinks() {
        final List<T> fff = getItems();
        final List<T> parentFFF = getParentItems();

        List<Hyperlink> removed = new ArrayList<>();
        List<Hyperlink> links = Stream.concat(
                parentFFF.stream().map(fa -> {

                    Hyperlink l = createLnk(fa, "#8b0201", "#8b4834", "#8b6c2b");
                    l.setOnAction(event -> {
                        Common.getInstance().getEventBus().post(onClicked(fa));
                    });
                    l.setOnContextMenuRequested(onParentRight(fa));

                    Hyperlink removeLink = new Hyperlink("-");
                    removed.add(removeLink);

                    return l;
                }),
                fff.stream().map(fa -> {

                    Hyperlink l = createLnk(fa, "#091a9c", "#0d69ff", "#898585");
                    l.setOnAction(event -> {
                        Common.getInstance().getEventBus().post(onClicked(fa));
                    });
                    l.setOnContextMenuRequested(onItemRight(fa, l));

                    Hyperlink removeLink = new Hyperlink("╳");
                    removeLink.setOnAction(onRemove(fa));
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

    private Hyperlink createLnk(T t, String color1, String color2, String color3) {
        Hyperlink l = new Hyperlink(name(t));

        switch (rank(t)) {
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

}
