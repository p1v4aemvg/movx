package com.by.movx.repository;

import com.by.movx.entity.Actor;
import com.by.movx.entity.Film;
import com.by.movx.entity.FilmActor;
import com.by.movx.entity.FilmLang;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmLangRepository extends CrudRepository<FilmLang, Long> {

    FilmLang findTop1ByFilmAndLang(Film film, FilmLang.Lang lang);
}
