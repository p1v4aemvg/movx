package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmLang;
import com.by.movx.event.Event;
import com.by.movx.repository.FilmLangRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class FilmLangPanel extends FilmTargetLinkPanel<FilmLang> {

    private FilmLangRepository flRepository;

    public FilmLangPanel(AnchorPane pane, Film film, FilmLangRepository flRepository) {
        super(pane, film);
        this.flRepository = flRepository;
    }

    @Override
    protected List<FilmLang> getItems(Film f) {
        return flRepository.findByFilm(f);
    }

    @Override
    protected int rank(FilmLang filmLang) {
        return filmLang.getSound() ? 1 : (filmLang.getSubtitled() ? 2 : 3);
    }

    @Override
    protected String name(FilmLang filmLang) {
        return filmLang.getLang().name() + (filmLang.getSound() ? "_ЗВУК" : "") + (filmLang.getSubtitled() ? "_СУБ" : "");
    }

    @Override
    protected Event<FilmLang> onClicked(FilmLang filmLang) {
        return new Event<FilmLang>(null) {
            @Override
            public FilmLang getData() {
                return null;
            }
        };
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onParentRight(FilmLang filmLang) {
        return e -> {};
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(FilmLang filmLang, Hyperlink l) {
        return e -> {};
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(FilmLang fl) {
        return event -> {
            flRepository.delete(fl);
            createLinks();
        };
    }
}
