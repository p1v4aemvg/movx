package com.by.movx.ui.common;

import com.by.movx.Common;
import com.by.movx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by movx
 * on 30.09.2017.
 */
public abstract class LinkPanel<T, F> {

    protected AnchorPane pane;
    protected F target;

    public LinkPanel(AnchorPane pane, F target) {
        this.pane = pane;
        this.target = target;
    }

    protected abstract List<T> getItems(F target);  // Stream<List<T>>

    protected abstract List<T> getParentItems();

    protected abstract int rank(T t);

    protected abstract String name(T t);

    protected abstract Event<T> onClicked(T t);

    protected abstract EventHandler<? super ContextMenuEvent> onParentRight(T t);

    protected abstract EventHandler<? super ContextMenuEvent> onItemRight(T t, Hyperlink l);

    protected abstract EventHandler<ActionEvent> onRemove(T t);

    protected Predicate<T> primaryPredicate() {
        return t -> true;
    }

    protected Predicate<T> parentPredicate(List<T> primaryItems) {
        return t -> true;
    }

    protected void sort(List<Hyperlink> links) {

    }

    public void createLinks() {
        final List<T> initialItems = getItems(target);
        final List<T> fff = initialItems.stream().filter(primaryPredicate()).collect(Collectors.toList());
        final List<T> parentFFF = getParentItems().stream().filter(parentPredicate(initialItems)).collect(Collectors.toList());
        List<Hyperlink> removed = new ArrayList<>();
        List<Hyperlink> links = Stream.concat(
                parentFFF.stream().map(fa -> linkWithAction(fa, null, removed)),
                fff.stream().map(fa -> linkWithAction(fa, onRemove(fa), removed))
        ).collect(Collectors.toList());

        sort(links);
        sort(removed);

        for (int i = 0; i < links.size(); i++) {
            links.get(i).setLayoutY(20 * i);
            links.get(i).setLayoutX(12);
            links.get(i).setFont(new Font("Courier New", 12));

            removed.get(i).setLayoutY(20 * i);
            removed.get(i).setFont(new Font("Courier New", 12));
        }

        pane.getChildren().clear();
        pane.getChildren().addAll(links);
        pane.getChildren().addAll(removed);
        pane.setPrefHeight(20 * links.size());
    }

    private Hyperlink linkWithAction(T fa, EventHandler<ActionEvent> action, List<Hyperlink> removed) {
        Hyperlink l = createLnk(fa, "#091a9c", "#0d69ff", "#898585");
        l.setOnAction(event -> {
            Common.getInstance().getEventBus().post(onClicked(fa));
        });
        l.setOnContextMenuRequested(action != null ? onItemRight(fa, l) : onParentRight(fa));

        Hyperlink removeLink = new Hyperlink(action != null ? "╳" : " ");
        removeLink.setId("r" + l.getId());
        if (action != null)
            removeLink.setOnAction(action);
        removed.add(removeLink);

        return l;
    }

    private Hyperlink createLnk(T t, String color1, String color2, String color3) {
        Hyperlink l = new Hyperlink(name(t));

        int rank = rank(t);
        switch (rank) {
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
        l.setId("*" + rank + "*" + RandomStringUtils.randomAlphabetic(7));
        return l;
    }

}
