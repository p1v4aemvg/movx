package com.by.movx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 13.03.2018.
 */
public class Actors {
    private List<Actor> actors = new ArrayList<>();

    public void add(Actor actor) {
        this.actors.add(actor);
    }

    public void remove(Actor actor) {
        this.actors.remove(actor);
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void clear() {
        this.actors.clear();
    }
}
