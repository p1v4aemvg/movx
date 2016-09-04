package com.by.movx.entity;
import javax.persistence.*;

/**
 * Created by movx
 * on 09.02.2016.
 */
@Entity
@Table(name = "film_description")
public class FilmDescription {

    @Id
    @Column(name = "film_id")
    Long id;

    @OneToOne
    @JoinColumn (name = "film_id")
    Film film;

    @Column(name = "description")
    private String description;

    @Column(name = "comment")
    private String comment;

    @Column(name = "image_url")
    private String url;

    public FilmDescription() {}

    public FilmDescription(Film film) {
        this.film = film;
        this.id = film.getId();
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
