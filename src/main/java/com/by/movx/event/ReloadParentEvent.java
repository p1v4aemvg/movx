package com.by.movx.event;

import com.by.movx.entity.FilmActor;

public class ReloadParentEvent extends Event<Long> {
    public ReloadParentEvent(Long data) {
        super(data);
    }
}
