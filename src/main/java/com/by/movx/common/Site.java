package com.by.movx.common;

/**
 * Created by movx
 * on 12.08.2017.
 */
public enum Site {
    WIKI("wikipedia", " вики"),
    KP("kinopoisk", " кинопоиск"),
    KINO_TEATR("kino-teatr", " kino-teatr информация о фильме"),
    IVI("ivi", " ivi описание");
    private String contain;
    private String addSearch;

    Site(String contain, String addSearch) {
        this.contain = contain;
        this.addSearch = addSearch;
    }

    public String getContain() {
        return contain;
    }

    public String getAddSearch() {
        return addSearch;
    }
}
