package com.by.movx.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by movx
 * on 04.02.2016.
 */
@Entity
@Table
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mark")
    private Integer mark;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "year")
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Film parent;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parent")
    private Set<Film> children = new HashSet<>();

    @Column(name = "type")
    @Enumerated
    private Type type;

    @Column(name = "duration")
    @Enumerated
    private Duration duration;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "film")
    private FilmDescription description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "film_country", joinColumns = {
            @JoinColumn(name = "film_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "country_id",
                    nullable = false, updatable = false)})
    Set<Country> countries;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "film")
    private FilmColor color;

    @Column(name = "never_delete")
    private Boolean neverDelete;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "film_size")
    private Long filmSize;

    @Column(name = "special")
    private Boolean special;

    @Column(name = "quality")
    private Integer quality;

    public Film() {
    }

    public Film(String name, String enName, Integer year) {
        this.name = name;
        this.enName = enName;
        this.year = year;
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

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public FilmDescription getDescription() {
        return description;
    }

    public void setDescription(FilmDescription description) {
        this.description = description;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Film getParent() {
        return parent;
    }

    public void setParent(Film parent) {
        this.parent = parent;
    }

    public Set<Film> getChildren() {
        return children;
    }

    public void setChildren(Set<Film> children) {
        this.children = children;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FilmColor getColor() {
        return color;
    }

    public void setColor(FilmColor color) {
        this.color = color;
    }

    public Boolean isNeverDelete() {
        return neverDelete;
    }

    public void setNeverDelete(Boolean neverDelete) {
        this.neverDelete = neverDelete;
    }

    public Long getFilmSize() {
        return filmSize;
    }

    public void setFilmSize(Long filmSize) {
        this.filmSize = filmSize;
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public enum Type {
        UNKNOWN("UNKNOWN"),
        FILM("Худ фильмы"),
        MULT("Мультфильмы"),
        PLAY("Спектакли"),
        TV_PLAY("Телеспектакли"),
        DOCUMENTARY("Документальные"),
        OPERA("Оперы"),
        BALET("Балет"),
        AUDIO("Аудиоспектакли"),
        FILM_OPERA("Фильм-оперы"),
        CONCERT("Концерты"),
        FILM_BALET("Фильм-балеты");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Duration {
        UNKNOWN("UNKNOWN", ""),
        SHORT("☀", "Короткометражки"),
        FILM("☀☀", "Фильмы"),
        SERIAL("☀☀☀", "Сериалы");

        private String name;
        private String description;

        Duration(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public static Duration of(int index) {
            if (index < 0 || index >= values().length)
                return Duration.UNKNOWN;
            return values()[index];
        }
    }

}
