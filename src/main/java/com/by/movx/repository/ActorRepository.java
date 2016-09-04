package com.by.movx.repository;

import com.by.movx.entity.Actor;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface ActorRepository extends CrudRepository<Actor, Long> {
    Actor findByName(String name);
}
