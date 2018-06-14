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
}
