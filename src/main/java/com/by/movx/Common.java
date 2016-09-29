package com.by.movx;

import com.google.common.eventbus.EventBus;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

public class Common {

    private static Common ourInstance = new Common();
    private EventBus eventBus = new EventBus();

    private Common() {}

    public static Common getInstance() {
        return ourInstance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}
