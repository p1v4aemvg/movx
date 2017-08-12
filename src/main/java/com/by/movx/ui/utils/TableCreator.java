package com.by.movx.ui.utils;

import com.by.movx.entity.Film;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.Date;

/**
 * Created by movx
 * on 12.08.2017.
 */
public class TableCreator {

    public static TableColumn<Film, ?>[] createColumns() {
        TableColumn<Film, HBox> countryColumn = new TableColumn<>("Country");
        countryColumn.setCellValueFactory
                (c -> new SimpleObjectProperty<>(UIUtils.wrapCountries(c)));

        TableColumn<Film, String> generalName = new TableColumn<>("Общее название");
        generalName.setCellValueFactory(c -> new SimpleObjectProperty<>(parent(c)));

        TableColumn<Film, String> nameColumn = new TableColumn<>("Нахвание");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Film, Integer> yearColumn = new TableColumn<>("Год");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Film, Date> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Film, String> folderColumn = new TableColumn<>("Папка");
        folderColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(folder(c)));

        TableColumn<Film, String> durationColumn = new TableColumn<>("Тип");
        durationColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(duration(c)));

        return new TableColumn[] {countryColumn, generalName, nameColumn,
                yearColumn, dateColumn, folderColumn, durationColumn};
    }

    private static String folder(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getType().getName();
    }

    private static String duration(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getDuration().getName();
    }

    private static String parent(TableColumn.CellDataFeatures<Film, String> c) {
        return c.getValue().getParent() == null ? "" : c.getValue().getParent().getName();
    }
}
