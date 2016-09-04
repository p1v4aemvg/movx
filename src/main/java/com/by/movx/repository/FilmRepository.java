package com.by.movx.repository;

import com.by.movx.entity.Country;
import com.by.movx.entity.Director;
import com.by.movx.entity.Film;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByYear (Integer year, Pageable pageable);

    @Query(value = "select f from FilmActor fa " +
            "left join fa.film as f " +
            "left join fa.actor as a " +
            "where a.name like  :actorName  ")
    List<Film> findByActor(@Param(value = "actorName") String actorName);

    List<Film> findByYearBetween (int year1, int year2);

    @Query("select f from Film f where :country member f.countries")
    List<Film> findByCountry (@Param(value = "country") Country country);

    List<Film> findByDirector (Director director);

    List<Film> findFirst10ByMarkOrderByIdDesc (Integer mark);

    List<Film> findByMark (Integer mark);

    @Query(value = "select f.* from film f order by rand() limit 1", nativeQuery = true)
    Film findRandomFilm();

    @Query(value = "select f.mark, count(f.id) from film f " +
            "group by f.mark", nativeQuery = true)
    List<Object[]> markStats();

    @Query(value = "select f.year, count(f.id) from film f " +
            "group by f.year", nativeQuery = true)
    List<Object[]> yearStats();

    @Query(value = "SELECT substring(f.name, 1, 1), count(f.id) from film f " +
            "group by substring(f.name, 1, 1) " +
            "order by substring(f.name, 1, 1)", nativeQuery = true)
    List<Object[]> filmsBy1stLetter();

    @Query(value = "select f.* from film f " +
            "join film_tag ft on f.id = ft.film_id " +
            "join tag t on ft.tag_id = t.id " +
            "where t.name = :tag " +
            "order by f.year", nativeQuery = true)
    List<Film> getFilmsByTag(@Param(value = "tag") String tag);

    @Query(value = "select f from Film f where f.name like :letter% or f.enName like :letter% ")
    List<Film> getFilmsBy1stLetter(@Param(value = "letter") String letter);

}
