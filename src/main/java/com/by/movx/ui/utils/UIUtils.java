package com.by.movx.ui.utils;

import com.by.movx.entity.Film;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;

/**
 * Created by movx
 * on 12.08.2017.
 */
public class UIUtils {

    public static HBox wrapCountries(TableColumn.CellDataFeatures<Film, HBox> c) {
        String cssBordering = "-fx-border-color:darkblue ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:7;\n"
                + "-fx-border-width:1.0";

        HBox g = new HBox();
        c.getValue().getCountries().stream()
                .map(k -> {
                            BorderPane p = new BorderPane();
                            Image im = new Image(new ByteArrayInputStream(k.getImage()));
                            ImageView iv = new ImageView(im);

                            p.setCenter(iv);
                            p.setMaxHeight(im.getHeight() + 5);
                            p.setMaxWidth(im.getWidth() + 5);

                            p.setStyle(cssBordering);
                            return p;
                        }
                )
                .forEach(p -> g.getChildren().add(p)
                );
        return g;
    }

    public static TableRow<Film> boldRow() {
        return new TableRow<Film>() {
            @Override
            protected void updateItem(Film film, boolean empty) {
                super.updateItem(film, empty);
                if (!empty) {
                    styleProperty().bind(Bindings.when(new SimpleBooleanProperty(film.getParent() == null))
                            .then("-fx-font-weight: bold;")
                            .otherwise(""));
                }
            }
        };
    }
}
