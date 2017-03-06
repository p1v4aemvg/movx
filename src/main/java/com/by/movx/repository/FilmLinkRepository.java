package com.by.movx.repository;

import com.by.movx.entity.Film;
import com.by.movx.entity.FilmLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmLinkRepository extends CrudRepository<FilmLink, Long> {

    List<FilmLink> findByDest(@Param(value = "dest") Film dest);
}
