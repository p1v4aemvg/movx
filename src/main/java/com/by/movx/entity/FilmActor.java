package com.by.movx.entity;

import javax.persistence.*;

/**
 * Created by movx
 * on 04.02.2016.
 */
@Entity
@Table(name = "film_actor")
public class FilmActor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "part_name")
    String partName;

    @Column(name = "absent")
    Boolean absent;

    public FilmActor() {
    }

    public FilmActor(Film film, Actor actor, Part part, String partName, Boolean absent) {
        this.film = film;
        this.actor = actor;
        this.part = part;
        this.partName = partName;
        this.absent = absent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Boolean getAbsent() {
        return absent;
    }

    public void setAbsent(Boolean absent) {
        this.absent = absent;
    }
}
