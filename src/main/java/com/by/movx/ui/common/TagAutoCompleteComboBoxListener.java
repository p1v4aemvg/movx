package com.by.movx.ui.common;

import com.by.movx.entity.Tag;
import com.by.movx.repository.TagRepository;
import javafx.scene.control.ComboBox;

import java.util.List;

/**
 * Created by Администратор
 * on 02.06.2016.
 */
public class TagAutoCompleteComboBoxListener extends AutoCompleteComboBoxListener<Tag> {
    private TagRepository tagRepository;

    public TagAutoCompleteComboBoxListener(ComboBox comboBox, TagRepository tagRepository) {
        super(comboBox);
        this.tagRepository = tagRepository;
    }

    @Override
    protected List<Tag> getData(String name) {
        return tagRepository.findByNameIgnoreCaseContaining(name);
    }

    @Override
    protected void create(String name) {
        if(!tagRepository.isExistsByName(name)) {
            Tag t = new Tag();
            t.setName(name);
            tagRepository.save(t);
        }
    }
}
