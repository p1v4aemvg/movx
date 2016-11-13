package com.by.movx.repository;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmTagRepository extends CrudRepository<FilmTag, Long> {
    List<FilmTag> findByFilm(@Param(value = "film") Film film);
}
