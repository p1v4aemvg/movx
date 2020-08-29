package com.by.movx.ui.common;

import com.by.movx.entity.CustomQuery;
import com.by.movx.event.Event;
import com.by.movx.event.StatActorClickedEvent;
import com.by.movx.event.StatFilmClickedEvent;
import com.by.movx.service.QueryEvaluator;
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
public class StatLinkPanel extends LinkPanel<Object[], Object> {

    private QueryEvaluator queryEvaluator;

    private CustomQuery customQuery;

    public StatLinkPanel(AnchorPane pane, QueryEvaluator queryEvaluator, CustomQuery customQuery) {
        super(pane, null);
        this.queryEvaluator = queryEvaluator;
        this.customQuery = customQuery;
    }

    @Override
    protected List<Object[]> getItems(Object target) {
        return queryEvaluator.getStats(customQuery);
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
        if(customQuery.getEntityType() == CustomQuery.EntityType.actor) {
            return new StatActorClickedEvent(objects);
        } else if(customQuery.getEntityType() == CustomQuery.EntityType.film) {
            return new StatFilmClickedEvent(objects);
        } else {
            return null;
        }
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
