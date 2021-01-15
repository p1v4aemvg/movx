package com.by.movx.repository;

import com.by.movx.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by movx
 * on 04.02.2016.
 */
public interface TagRepository extends CrudRepository<Tag, Long> {

    List<Tag> findByNameIgnoreCaseContaining(String name);

    @Query(value = "select t.* from tag t order by rand() limit 1", nativeQuery = true)
    Tag findRand();

    @Query(value = "select count(t)>0 from Tag t where t.name = :name")
    Boolean isExistsByName(@Param("name") String name);

    @Query(value = "select t.* from tag t where t.tag_type_id = 8", nativeQuery = true)
    List<Tag> findCumulativeTags();

    @Query(value = "select t.* from tag t where t.tag_type_id = 12", nativeQuery = true)
    List<Tag> findTimeTags();


}
