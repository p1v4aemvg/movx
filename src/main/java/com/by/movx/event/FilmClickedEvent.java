package com.by.movx.event;

import com.by.movx.entity.Film;

public class FilmClickedEvent extends Event<Film> {
    public FilmClickedEvent(Film data) {
        super(data);
    }
}
