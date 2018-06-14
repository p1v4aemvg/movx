package com.by.movx.service;

import com.by.movx.entity.CustomQuery;
import com.by.movx.entity.Film;
import com.by.movx.repository.FilmRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by movx
 * on 26.07.2017.
 */

@Component
public class QueryEvaluator {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FilmRepository filmRepository;

    public List<Film> getFilms(CustomQuery cq) {
        Query q = entityManager.createNativeQuery(cq.getQuery());
        List objects = q.getResultList();
        List<Long> ids = new ArrayList<>();

        for(Object o : objects) {
            ids.add(Long.valueOf(o.toString()));
        }

        String params[] = StringUtils.split(cq.getOrderBy(), ", ");
        return filmRepository.findByIdIn(ids,
                new Sort(Sort.Direction.valueOf(params[1].toUpperCase()), params[0]));
    }

    public List<Object[]> getStats(CustomQuery cq) {
        Query q = entityManager.createNativeQuery(cq.getQuery());
        return (List<Object[]>)q.getResultList();
    }

    public Long getFilmsCount(CustomQuery cq) {
        String query = cq.getQuery();
        query = query.replaceAll("distinct f.id", "count(distinct f.id)");
        query = query.replaceAll("distinct f1.id", "count(distinct f1.id)");

        Query q = entityManager.createNativeQuery(query);
        List objects = q.getResultList();
        if(objects.isEmpty()) return 0L;
        return Long.valueOf(objects.get(0).toString());
    }


}
