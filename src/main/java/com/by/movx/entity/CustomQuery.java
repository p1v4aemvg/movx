package com.by.movx.entity;

import javax.persistence.*;

/**
 * Created by movx
 * on 26.07.2017.
 */
@Entity
@Table (name = "custom_query")
public class CustomQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "query_name")
    private String name;

    @Column(name = "query_string")
    private String query;

    public CustomQuery() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
