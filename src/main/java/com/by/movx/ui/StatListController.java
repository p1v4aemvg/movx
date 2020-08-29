package com.by.movx.ui;

import com.by.movx.entity.CustomQuery;
import com.by.movx.service.QueryEvaluator;
import com.by.movx.ui.common.StatLinkPanel;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

/**
 * Created by movx
 * on 20.05.2018.
 */
public class StatListController {
    @FXML
    AnchorPane pane;

    @Inject
    QueryEvaluator queryEvaluator;

    private CustomQuery customQuery;

    public void init() {
        StatLinkPanel statLinkPanel = new StatLinkPanel(pane, queryEvaluator, customQuery);
        statLinkPanel.createLinks();
    }

    public void setCustomQuery(CustomQuery customQuery) {
        this.customQuery = customQuery;
    }
}
