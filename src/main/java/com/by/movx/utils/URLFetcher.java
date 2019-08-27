package com.by.movx.utils;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;

/**
 * Created by movx
 * on 12.08.2017.
 */

@Component
public class URLFetcher {

    public String googleQ(String s) {
        s = s.replaceAll("[^0-9a-zA-Zа-яА-ЯёЁ\\s]", "");
        s = s.replaceAll("\\s", "+");
        return "https://www.google.by/search?q=" + s;
    }
}
