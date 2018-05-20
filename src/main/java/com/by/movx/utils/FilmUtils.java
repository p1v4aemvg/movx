package com.by.movx.utils;

import com.by.movx.entity.Film;
import org.apache.commons.lang.StringUtils;

import java.util.function.Function;

/**
 * Created by movx
 * on 20.05.2018.
 */
public class FilmUtils {
    public static String name(Film film, Function<Film, String> field) {
        if(film == null) return StringUtils.EMPTY;
        String name = field.apply(film);
        if (StringUtils.isBlank(name)) return StringUtils.EMPTY;
        int index = name.indexOf('~');
        if(index == -1) {
            return name;
        }
        return name.substring(0, index);
    }
}
