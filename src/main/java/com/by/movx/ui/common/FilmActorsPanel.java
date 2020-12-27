package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmActor;
import com.by.movx.event.ActorClickedEvent;
import com.by.movx.event.Event;
import com.by.movx.repository.FilmActorRepository;
import com.by.movx.utils.FilmUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class FilmActorsPanel extends FilmTargetLinkPanel<FilmActor> {

    private FilmActorRepository faRepository;
    private boolean absent;

    public FilmActorsPanel(AnchorPane pane, Film film, boolean absent, FilmActorRepository faRepository) {
        super(pane, film);
        this.absent = absent;
        this.faRepository = faRepository;
    }

    protected int rank (FilmActor fa) {
        return fa.getPart().getId().intValue();
    }

    protected String name (FilmActor fa) {
        return FilmUtils.fullName(fa, target);
    }

    @Override
    protected Event<FilmActor> onClicked(FilmActor fa) {
        return new ActorClickedEvent(fa);
    }

    @Override
    protected EventHandler<ContextMenuEvent> onParentRight(FilmActor fa) {
        return event -> {
            fa.setFilm(target);
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
                    l.setText(FilmUtils.fullName(fa, target));
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

    protected Predicate<FilmActor> primaryPredicate() {
        return fa -> fa.getAbsent().equals(absent);
    }

    protected Predicate<FilmActor> parentPredicate(List<FilmActor> primaryItems) {
        return fa -> !absent && !fa.getAbsent() &&
                primaryItems.stream().noneMatch(pa -> pa.getAbsent() && pa.getActor().getId().equals(fa.getActor().getId()));
    }

    protected void sort(List<Hyperlink> links) {
        links.sort((l1, l2) -> getRank(l1).compareTo(getRank(l2)));
    }

    private String getRank(Hyperlink hyperlink) {
        return StringUtils.substringBetween(hyperlink.getId(), "*", "*");
    }
}
