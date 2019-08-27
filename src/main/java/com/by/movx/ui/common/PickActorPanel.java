package com.by.movx.ui.common;

import com.by.movx.Common;
import com.by.movx.entity.Actor;
import com.by.movx.entity.Actors;
import com.by.movx.event.Event;
import com.by.movx.event.PickUpdateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

/**
 * Created by movx
 * on 30.09.2017.
 */
public class PickActorPanel extends ActorTargetLinkPanel<Actor> {

    public PickActorPanel(AnchorPane pane, Actors actors) {
        super(pane, actors);
    }

    @Override
    protected List<Actor> getItems(Actors actors) {
        return actors.getActors();
    }

    @Override
    protected int rank(Actor actor) {
        return 1;
    }

    @Override
    protected String name(Actor actor) {
        return actor.getName();
    }

    @Override
    protected Event<Actor> onClicked(Actor actor) {
        return new Event<Actor>(null) {
            @Override
            public Actor getData() {
                return null;
            }
        };
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onParentRight(Actor actor) {
        return e -> {};
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(Actor actor, Hyperlink l) {
        return e -> {};
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(Actor actor) {
        return event -> {
            target.remove(actor);
            Common.getInstance().getEventBus().post(new PickUpdateEvent());
        };
    }
}
