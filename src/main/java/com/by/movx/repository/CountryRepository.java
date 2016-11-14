package com.by.movx.repository;

import com.by.movx.entity.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by movx
 * on 09.02.2016.
 */
public interface CountryRepository extends CrudRepository<Country, Long> {

    @Query(value = "select c.name, count(fc.film_id) from country c\n" +
            "join film_country fc on fc.country_id = c.id\n " +
            "join film f on f.id = fc.film_id " +
            "where f.count_in_stat = 1 " +
            "group by fc.country_id order by count(fc.film_id) desc", nativeQuery = true)
    List<Object[]> loadCountryStat();
}
