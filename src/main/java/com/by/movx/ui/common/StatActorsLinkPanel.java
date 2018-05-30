package com.by.movx.ui.common;

import com.by.movx.event.Event;
import com.by.movx.event.StatActorClickedEvent;
import com.by.movx.repository.ActorRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 20.05.2018.
 */
public class StatActorsLinkPanel extends LinkPanel<Object[], Object> {

    private ActorRepository actorRepository;

    public StatActorsLinkPanel(AnchorPane pane, ActorRepository actorRepository) {
        super(pane, null);
        this.actorRepository = actorRepository;
    }

    @Override
    protected List<Object[]> getItems(Object target) {
        return actorRepository.actorsByRoles();
    }

    @Override
    protected List<Object[]> getParentItems() {
        return new ArrayList<>();
    }

    @Override
    protected int rank(Object[] objects) {
        return 1;
    }

    @Override
    protected String name(Object[] objects) {
        return objects[1] + " " + objects[0];
    }

    @Override
    protected Event<Object[]> onClicked(Object[] objects) {
        return new StatActorClickedEvent(objects);
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onParentRight(Object[] objects) {
        return e -> {};
    }

    @Override
    protected EventHandler<? super ContextMenuEvent> onItemRight(Object[] objects, Hyperlink l) {
        return e -> {};
    }

    @Override
    protected EventHandler<ActionEvent> onRemove(Object[] objects) {
        return e -> {};
    }
}
