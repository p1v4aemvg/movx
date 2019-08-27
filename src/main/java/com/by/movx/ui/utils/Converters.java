package com.by.movx.ui.utils;

import com.by.movx.entity.*;
import com.by.movx.utils.FilmUtils;
import javafx.util.StringConverter;

/**
 * Created by movx
 * on 27.08.2019.
 */
public class Converters {
    public static StringConverter<Country> countryStringConverter() {
        return new StringConverter<Country>() {
            @Override
            public String toString(Country object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        };
    }

    public static StringConverter<Tag> tagStringConverter() {
        return new StringConverter<Tag>() {
            @Override
            public String toString(Tag object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Tag fromString(String string) {
                return null;
            }
        };
    }

    public static StringConverter<Film.Type> filmTypeStringConverter() {
        return new StringConverter<Film.Type>() {
            @Override
            public String toString(Film.Type object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Film.Type fromString(String string) {
                return null;
            }
        };
    }

    public static StringConverter<Film> filmStringConverter() {
        return new StringConverter<Film>() {
            @Override
            public String toString(Film object) {
                return object == null ? null :
                        object.getYear() + " " + FilmUtils.name(object, Film::getName);
            }

            @Override
            public Film fromString(String string) {
                return null;
            }
        };
    }

    public static StringConverter<CustomQuery> customQueryStringConverter() {
        return new StringConverter<CustomQuery>() {
            @Override
            public String toString(CustomQuery object) {
                return object == null ? null : object.getName();
            }

            @Override
            public CustomQuery fromString(String string) {
                return null;
            }

        };
    }

    public static  StringConverter<FilmLang.Lang> filmLangConverter() {
        return new StringConverter<FilmLang.Lang>() {
            @Override
            public String toString(FilmLang.Lang object) {
                return object == null ? null : object.name();
            }

            @Override
            public FilmLang.Lang fromString(String string) {
                return null;
            }
        };
    }
}
