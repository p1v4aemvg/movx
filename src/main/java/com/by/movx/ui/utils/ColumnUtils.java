package com.by.movx.ui.utils;

import com.by.movx.entity.Film;
import com.by.movx.utils.FilmUtils;
import javafx.scene.control.TableColumn;

/**
 * Created by movx
 * on 27.08.2019.
 */
public class ColumnUtils {
    public static String name(TableColumn.CellDataFeatures<Film, String> c) {
        return FilmUtils.name(c.getValue(), Film::getName);
    }

    public static String folder(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getType().getName();
    }

    public static String duration(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getDuration().getName();
    }

    public static Integer quality(TableColumn.CellDataFeatures<Film, Integer> c) {
        return c.getValue().getQuality();
    }

    public static String parent(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getParent() == null ? "" : FilmUtils.name(c.getValue().getParent(), Film::getName);
    }

    public static Integer size(TableColumn.CellDataFeatures<Film, Integer> c) {
        return c.getValue().getFilmSize() == null ? 0 : Long.valueOf(c.getValue().getFilmSize()/(1024*1024)).intValue();
    }
}
