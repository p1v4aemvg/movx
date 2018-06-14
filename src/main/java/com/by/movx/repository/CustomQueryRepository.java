package com.by.movx.repository;

import com.by.movx.entity.CustomQuery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface CustomQueryRepository extends CrudRepository<CustomQuery, Long> {
    List<CustomQuery> findByQueryType(CustomQuery.QueryType queryType);
}
