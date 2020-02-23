package com.by.movx.repository;

import com.by.movx.entity.Actor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface ActorRepository extends CrudRepository<Actor, Long> {
    Actor findByName(String name);

    @Query(value="SELECT a.* FROM actor a where a.img is null order by rand() limit 10", nativeQuery = true)
    List<Actor> findRand10();

    @Query(value = "select count(*) from actor where img is null", nativeQuery = true)
    Long getNoImgCount();

    @Query(value = "select count(*) from actor where born is null", nativeQuery = true)
    Long getNoBornCount();

    @Query(value = "select a.name, count(fa.film), a.id from FilmActor fa " +
            "join fa.actor a " +
            "group by fa.actor " +
            "order by count(fa.film.id) desc")
    List<Object[]> actorsByRoles();

    @Query(value="SELECT a.* FROM actor a where a.born is null order by rand() limit 10", nativeQuery = true)
    List<Actor> find10NoYear();
}
