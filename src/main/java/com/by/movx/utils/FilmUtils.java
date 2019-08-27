package com.by.movx.utils;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmActor;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
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

    public static String film(FilmActor fa) {
        String name = FilmUtils.name(fa.getFilm(), Film::getName);
        return fa.getFilm().getYear().toString() + " " + name + " " + age(fa, fa.getFilm()) +
                (fa.getPartName() == null ? "" : (repeat(66 + pad(fa) - name.length() - fa.getPartName().length()) + fa.getPartName()));
    }

    public static String fullName(FilmActor fa, Film film1) {
        return age(fa, film1) + " " + fa.getActor().getName()
                + (fa.getPartName() == null ? "" : (repeat(50 + pad(fa) - fa.getActor().getName().length() - fa.getPartName().length()) + fa.getPartName()));
    }

    private static String age(FilmActor fa, Film film1) {
        return fa.getActor().getBorn() == null ? "" : "(" + (film1.getYear() - fa.getActor().getBorn()) + ")";
    }

    private static int pad(FilmActor fa) {
        return fa.getActor().getBorn() == null ?
                0 : -(String.valueOf(fa.getFilm().getYear() - fa.getActor().getBorn()).length() + 3);
    }

    private static String repeat(int n) {
        if (n <= 0) return " ";
        char arr[] = new char[n];
        Arrays.fill(arr, ' ');
        return new String(arr);
    }
}
