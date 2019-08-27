package com.by.movx;

import com.by.movx.ui.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class ConfigurationControllers {

    @Bean(name = "mainView")
    public View getMainView() throws IOException {
        return loadView("fxml/main.fxml");
    }

    @Bean(name = "fdView")
    public View getFDView() throws IOException {
        return loadView("fxml/film_description1.fxml");
    }

    @Bean(name = "diagView")
    public View getDiagView() throws IOException {
        return loadView("fxml/diag1.fxml");
    }

    @Bean(name = "actorView")
    public View getActorView() throws IOException {
        return loadView("fxml/actors.fxml");
    }


    @Bean(name = "addView")
    public View getAddView() throws IOException {
        return loadView("fxml/add.fxml");
    }

    @Bean(name = "statActorsView")
    public View getStatActorsView() throws IOException {
        return loadView("fxml/actor_stat.fxml");
    }

    @Bean
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/video?autoReconnect=true&characterEncoding=UTF-8");
        dataSource.setPassword("root");
        dataSource.setUser("root");
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public MainController getMainController() throws IOException {
        return (MainController) getMainView().getController();
    }

    @Bean
    public FDController getFDController() throws IOException {
        return (FDController) getFDView().getController();
    }

    @Bean
    public DiagController getDiagController() throws IOException {
        return (DiagController) getDiagView().getController();
    }

    @Bean
    public ActorController getActorController() throws IOException {
        return (ActorController) getActorView().getController();
    }

    @Bean
    public AddController getAddController() throws IOException {
        return (AddController) getAddView().getController();
    }

    @Bean StatActorsController getStatActorsConroller() throws IOException {
        return (StatActorsController) getStatActorsView().getController();
    }

    public View loadView(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getClassLoader().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return new View(loader.getRoot(), loader.getController());
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }

    public class View {
        private Parent view;
        private Object controller;

        public View(Parent view, Object controller) {
            this.view = view;
            this.controller = controller;
        }

        public Parent getView() {
            return view;
        }

        public void setView(Parent view) {
            this.view = view;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

    }

}
