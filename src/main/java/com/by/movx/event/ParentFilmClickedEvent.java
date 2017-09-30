package com.by.movx.event;

import com.by.movx.entity.Film;

public class ParentFilmClickedEvent extends Event<Film> {
    public ParentFilmClickedEvent(Film data) {
        super(data);
    }
}
