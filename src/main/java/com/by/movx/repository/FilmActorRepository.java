package com.by.movx.repository;

import com.by.movx.entity.Actor;
import com.by.movx.entity.Film;
import com.by.movx.entity.FilmActor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmActorRepository extends CrudRepository<FilmActor, Long> {
    @Query(value = "select fa from FilmActor fa " +
            "where fa.actor = :actor " +
            "and fa.absent = false " +
            "order by fa.film.year")
    List<FilmActor> findByActor(@Param(value = "actor") Actor actor);

    FilmActor findTop1ByActorAndFilm(Actor actor, Film film);

    @Query(value = "select fa from FilmActor fa " +
            "where fa.film = :film " +
            "order by fa.part.id, fa.id")
    List<FilmActor> findByFilm(@Param(value = "film")Film film);
}
