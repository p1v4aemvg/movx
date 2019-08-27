package com.by.movx.ui.common;

import com.by.movx.entity.Actor;
import com.by.movx.entity.Actors;
import com.by.movx.entity.FilmActor;
import com.by.movx.event.Event;
import com.by.movx.event.FilmClickedEvent;
import com.by.movx.repository.FilmActorRepository;
import com.by.movx.utils.FilmUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class ActorFilmsPanel extends ActorTargetLinkPanel<FilmActor> {

    public enum Mode {
        UNION, INTERSECT
    }

    private FilmActorRepository faRepository;
    private Mode mode;

    public ActorFilmsPanel(AnchorPane pane, Actors actors, FilmActorRepository faRepository) {
        this(pane, actors, faRepository, Mode.UNION);
    }

    public ActorFilmsPanel(AnchorPane pane, Actors actors, FilmActorRepository faRepository, Mode mode) {
        super(pane, actors);
        this.faRepository = faRepository;
        this.mode = mode;
    }

    protected int rank (FilmActor fa) {
        return fa.getPart().getId().intValue();
    }

    protected String name (FilmActor fa) {
        return FilmUtils.film(fa);
    }

    @Override
    protected Event<FilmActor> onClicked(FilmActor fa) {
        return new FilmClickedEvent(fa);
    }

    @Override
    protected EventHandler<ContextMenuEvent> onParentRight(FilmActor fa) {
        return event -> {};

    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(FilmActor fa, Hyperlink l) {
        return event -> {
            TextField temp = new TextField();
            temp.setPrefHeight(15);

            temp.setLayoutY(l.getLayoutY());
            temp.setLayoutX(345);
            temp.setOnAction(event1 -> {
                if (!temp.getText().isEmpty()) {
                    fa.setPartName(temp.getText());
                    l.setText(FilmUtils.film(fa));
                    faRepository.save(fa);
                }
                pane.getChildren().remove(temp);
            });
            pane.getChildren().add(temp);
        };
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(FilmActor fa) {
        return event -> {};
    }

    public List<FilmActor> getItems(Actors actors) {
        List<FilmActor> fas = new ArrayList<>();

        for (Actor a : actors.getActors()) {
            fas.addAll(faRepository.findByActor(a));
        }

        if(mode == Mode.UNION) {
            return fas.stream()
                    .collect(Collectors.groupingBy(fa -> fa.getFilm().getId())).entrySet()
                    .stream().map(e -> e.getValue().get(0))
                    .sorted((fa1, fa2) -> Integer.compare(fa1.getFilm().getYear(), fa2.getFilm().getYear()))
                    .collect(Collectors.toList());
        }

        return fas.stream()
                .collect(Collectors.groupingBy(fa -> fa.getFilm().getId())).entrySet()
                .stream()
                .filter(e -> e.getValue().size() == actors.getActors().size())
                .map(e -> e.getValue().get(0))
                .sorted((fa1, fa2) -> Integer.compare(fa1.getFilm().getYear(), fa2.getFilm().getYear()))
                .collect(Collectors.toList());
    }
}
