package com.by.movx.event;

import com.by.movx.entity.FilmActor;

public class FilmClickedEvent extends Event<FilmActor> {
    public FilmClickedEvent(FilmActor data) {
        super(data);
    }
}
