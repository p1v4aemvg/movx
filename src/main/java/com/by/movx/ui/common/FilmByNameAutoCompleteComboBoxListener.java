package com.by.movx.ui.common;

import com.by.movx.entity.Film;
import com.by.movx.entity.Tag;
import com.by.movx.repository.FilmRepository;
import com.by.movx.repository.TagRepository;
import javafx.scene.control.ComboBox;

import java.util.List;

/**
 * Created by Администратор
 * on 02.06.2016.
 */
public class FilmByNameAutoCompleteComboBoxListener extends AutoCompleteComboBoxListener<Film> {
    private FilmRepository filmRepository;

    public FilmByNameAutoCompleteComboBoxListener(ComboBox comboBox, FilmRepository filmRepository) {
        super(comboBox);
        this.filmRepository = filmRepository;
    }

    @Override
    protected List<Film> getData(String name) {
        return filmRepository.findByNameIgnoreCaseContaining(name);
    }

    @Override
    protected void create(String name) {

    }
}
