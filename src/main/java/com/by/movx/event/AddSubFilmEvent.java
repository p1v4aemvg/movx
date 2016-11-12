package com.by.movx.event;

import com.by.movx.entity.Film;

public class AddSubFilmEvent extends Event<Film> {
    public AddSubFilmEvent(Film data) {
        super(data);
    }
}
