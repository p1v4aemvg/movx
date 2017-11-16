package com.by.movx.entity;

import javax.persistence.*;
import java.util.Arrays;

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

    public FilmActor() {
    }

    public FilmActor(Film film, Actor actor, Part part, String partName) {
        this.film = film;
        this.actor = actor;
        this.part = part;
        this.partName = partName;
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

    public String fullName(Film film1) {
        return age(film1) + " " + actor.getName() + (partName == null ? "" : (repeat(50 + pad() - actor.getName().length() - partName.length()) + partName));
    }

    public String film() {
        return film.getYear().toString() + " " + film.getName() + " " + age(film) +
                (partName == null ? "" : (repeat(66 + pad() - film.getName().length() - partName.length()) + partName));
    }

    private String age(Film film1) {
        return actor.getBorn() == null ? "" : "(" + (film1.getYear() - actor.getBorn()) + ")";
    }

    private int pad() {
        return actor.getBorn() == null ? 0 : -(String.valueOf(film.getYear() - actor.getBorn()).length() + 3);
    }

    private String repeat(int n) {
        if (n <= 0) return " ";
        char arr[] = new char[n];
        Arrays.fill(arr, ' ');
        return new String(arr);
    }

    public String uq() {
        return film.getId() + " " + actor.getName();
    }
}
