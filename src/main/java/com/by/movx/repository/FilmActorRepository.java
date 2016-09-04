package com.by.movx.repository;

import com.by.movx.entity.Actor;
import com.by.movx.entity.FilmActor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmActorRepository extends CrudRepository<FilmActor, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE fa " +
            "  FROM film_actor fa JOIN " +
            "( " +
            "  SELECT film_id, actor_id, MAX(id) id1 " +
            "    FROM film_actor " +
            "   GROUP BY film_id, actor_id " +
            ") d  " +
            "   ON " +
            " (fa.film_id = d.film_id  " +
            "and fa.actor_id = d.actor_id " +
            "  AND fa.id <> d.id1) " +
            "where   fa.id > 0 ", nativeQuery = true)
    void deleteDuplicates();

    List<FilmActor> findByActor(Actor actor);
}
