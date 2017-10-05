package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmTag;
import com.by.movx.event.Event;
import com.by.movx.event.TagClickedEvent;
import com.by.movx.repository.FilmTagRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class TagsPanel extends LinkPanel<FilmTag> {

    private FilmTagRepository ftRepository;

    public TagsPanel(AnchorPane pane, Film film, FilmTagRepository ftRepository) {
        super(pane, film);
        this.ftRepository = ftRepository;
    }

    @Override
    protected List<FilmTag> getItems(Film f) {
        return ftRepository.findByFilm(f);
    }

    @Override
    protected int rank(FilmTag filmTag) {
        return 2;
    }

    @Override
    protected String name(FilmTag ft) {
        return ft.getTag().getName();
    }

    @Override
    protected Event<FilmTag> onClicked(FilmTag ft) {
        return new TagClickedEvent(ft);
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onParentRight(FilmTag filmTag) {
        return e -> {};
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(FilmTag filmTag, Hyperlink l) {
        return e -> {};
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(FilmTag ft) {
        return event -> {
            ftRepository.delete(ft);
            createLinks();
        };
    }
}
