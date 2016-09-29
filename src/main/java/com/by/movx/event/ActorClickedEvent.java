package com.by.movx.event;

import com.by.movx.entity.FilmActor;

public class ActorClickedEvent extends Event<FilmActor> {
    public ActorClickedEvent(FilmActor data) {
        super(data);
    }
}
