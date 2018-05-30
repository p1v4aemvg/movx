package com.by.movx.event;

import com.by.movx.entity.FilmActor;

public class StatActorClickedEvent extends Event<Object[]> {
    public StatActorClickedEvent(Object[] data) {
        super(data);
    }
}
