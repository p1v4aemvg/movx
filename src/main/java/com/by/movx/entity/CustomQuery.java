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

    @Column(name = "order_by")
    private String orderBy;

    @Column(name = "cq_type")
    @Enumerated
    private CQType cqType;

    @Column(name = "query_type")
    @Enumerated
    private QueryType queryType;

    public CustomQuery() {}

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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public CQType getCqType() {
        return cqType;
    }

    public void setCqType(CQType cqType) {
        this.cqType = cqType;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public enum CQType {
        URGENT, MEDIUM, COLLECTION, RAND, NEUTRAL
    }

    public enum QueryType{
        FILM, STAT
    }
}
