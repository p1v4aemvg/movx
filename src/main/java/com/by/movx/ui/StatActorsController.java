package com.by.movx.ui;

import com.by.movx.repository.ActorRepository;
import com.by.movx.ui.common.StatActorsLinkPanel;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

/**
 * Created by movx
 * on 20.05.2018.
 */
public class StatActorsController {
    @FXML
    AnchorPane pane;

    @Inject
    ActorRepository actorRepository;

    public void init() {
        StatActorsLinkPanel statLinkPanel = new StatActorsLinkPanel(pane, actorRepository);
        statLinkPanel.createLinks();
    }
}
