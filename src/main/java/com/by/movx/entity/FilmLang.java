package com.by.movx.entity;

import javax.persistence.*;

/**
 * Created by movx
 * on 04.08.2017.
 */
@Entity
@Table(name = "film_lang")
public class FilmLang {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "lang_id")
    @Enumerated
    private Lang lang;

    @Column(name = "sound")
    private Boolean sound;

    @Column(name = "subtitled")
    private Boolean subtitled;

    public FilmLang() {

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

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public Boolean getSound() {
        return sound;
    }

    public void setSound(Boolean sound) {
        this.sound = sound;
    }

    public Boolean getSubtitled() {
        return subtitled;
    }

    public void setSubtitled(Boolean subtitled) {
        this.subtitled = subtitled;
    }

    public enum Lang {
        NO, RU, EN, BY, DE, FR
    }
}
