package com.by.movx.entity;
import javax.persistence.*;

/**
 * Created by movx
 * on 09.02.2016.
 */
@Entity
@Table(name = "film_color")
public class FilmColor {

    @Id
    @Column(name = "film_id")
    Long id;

    @OneToOne
    @JoinColumn (name = "film_id")
    Film film;

    @Column(name = "c1")
    private String c1 = "0xffffffff";

    @Column(name = "c2")
    private String c2 = "0xffffffff";

    @Column(name = "c3")
    private String c3 = "0xffffffff";

    @Column(name = "c4")
    private String c4 = "0xffffffff";

    public FilmColor() {}

    public FilmColor(Film film) {
        this.film = film;
        this.id = film.getId();
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
    public String getC1() {
        return c1;
    }

    public void setC1(String c1) {
        this.c1 = c1;
    }

    public String getC2() {
        return c2;
    }

    public void setC2(String c2) {
        this.c2 = c2;
    }

    public String getC3() {
        return c3;
    }

    public void setC3(String c3) {
        this.c3 = c3;
    }

    public String getC4() {
        return c4;
    }

    public void setC4(String c4) {
        this.c4 = c4;
    }
}
