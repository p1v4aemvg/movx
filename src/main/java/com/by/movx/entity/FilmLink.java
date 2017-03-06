package com.by.movx.entity;

import javax.persistence.*;

/**
 * Created by movx
 * on 04.02.2016.
 */
@Entity
@Table(name = "film_link")
public class FilmLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dest_id")
    private Film dest;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Film source;


    public FilmLink() {
    }

    public FilmLink(Film dest, Film source) {
        this.dest = dest;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Film getDest() {
        return dest;
    }

    public void setDest(Film dest) {
        this.dest = dest;
    }

    public Film getSource() {
        return source;
    }

    public void setSource(Film source) {
        this.source = source;
    }
}
