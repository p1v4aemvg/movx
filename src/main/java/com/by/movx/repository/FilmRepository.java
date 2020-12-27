package com.by.movx.repository;

import com.by.movx.entity.Country;
import com.by.movx.entity.Film;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface FilmRepository extends CrudRepository<Film, Long> {
    List<Film> findByIdIn(List<Long> idList, Sort sort);

    List<Film> findByYearBetween (int year1, int year2);

    @Query("select f from Film f where :country member f.countries")
    List<Film> findByCountry (@Param(value = "country") Country country);

    List<Film> findFirst10ByMarkOrderByIdDesc (Integer mark);

    List<Film> findByType(@Param(value = "type") Film.Type type);

    @Query("select f from Film f where f.createdAt is null")
    List<Film> filmsWithoutDate();

    @Query(value = "select f.* from film f order by rand() limit 1", nativeQuery = true)
    Film findRandomFilm();

    @Query(value = "select f.* from film f " +
            "join film_tag ft on f.id = ft.film_id " +
            "join tag t on ft.tag_id = t.id " +
            "where t.name = :tag " +
            "order by f.year", nativeQuery = true)
    List<Film> getFilmsByTag(@Param(value = "tag") String tag);

    @Query(value = "select f from Film f where f.name like :letter% or f.enName like :letter% ")
    List<Film> getFilmsBy1stLetter(@Param(value = "letter") String letter);

    @Query(value = "select count(f) from Film f where f.entity = true")
    Long countFilms();

    List<Film> findByNameIgnoreCaseContaining(String name);

    @Query(value = "select f.* from film f " +
            " left join film f1 on f1.id = f.parent_id " +
            " left join film f2 on f2.id = f1.parent_id " +
            "  where not exists (select * from film_tag ft where ft.tag_id = :tag_id and ft.film_id = f.id) " +
            "    and not exists(select * from film_tag ft where ft.film_id = f1.id and ft.tag_id = :tag_id) " +
            "   and not exists(select * from film_tag ft where ft.film_id = f2.id and ft.tag_id = :tag_id) " +
            "   and (f.id >= (select max(ft.film_id) from film_tag ft where ft.tag_id = :tag_id) " +
            "   or (select max(ft.film_id) from film_tag ft where ft.tag_id = :tag_id) is null)", nativeQuery = true)
    List<Film> findByCumulativeTag(@Param(value = "tag_id") Long tagId);

    @Query(value = "select f.* from film f " +
            "where not exists (select fd.* from film_description fd where f.id = fd.film_id)", nativeQuery = true)
    List<Film> findWithoutFD();
}
