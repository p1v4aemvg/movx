package com.by.movx.ui.common;

import com.by.movx.entity.Actors;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 13.03.2018.
 */
public abstract class ActorTargetLinkPanel<T> extends LinkPanel<T, Actors> {

    public ActorTargetLinkPanel(AnchorPane pane, Actors actors) {
        super(pane, actors);
    }

    protected List<T> getParentItems () {
        return new ArrayList<>();
    }
}
