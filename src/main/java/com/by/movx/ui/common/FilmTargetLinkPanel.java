package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 13.03.2018.
 */
public abstract class FilmTargetLinkPanel<T> extends LinkPanel<T, Film> {

    public FilmTargetLinkPanel(AnchorPane pane, Film film) {
        super(pane, film);
    }

    protected List<T> getParentItems () {
        Film f = target;
        final List<T> parentFFF = new ArrayList<>();
        while (f.getParent() != null) {
            parentFFF.addAll(getItems(f.getParent()));
            f = f.getParent();
        }
        return parentFFF;
    }
}
