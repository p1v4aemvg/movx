package com.by.movx.service;

import com.by.movx.entity.CustomQuery;
import com.by.movx.entity.Film;
import com.by.movx.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        return filmRepository.findByIdIn(ids);
    }


}
