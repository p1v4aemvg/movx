package com.by.movx.event;

import com.by.movx.entity.FilmTag;

public class TagClickedEvent extends Event<FilmTag> {
    public TagClickedEvent(FilmTag data) {
        super(data);
    }
}
