package com.by.movx.utils;

import org.codehaus.httpcache4j.uri.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;

/**
 * Created by movx
 * on 12.08.2017.
 */

@Component
public class URLFetcher {

    public String fetch(String query, String contain) throws Exception {
        Document doc = Jsoup.connect(query).userAgent("Chrome").get();
        Elements list = doc.getElementsByAttributeValueContaining("href", contain);
        if(!list.isEmpty()) {
            String url = list.attr("abs:href");
            return URLDecoder.decode(URIBuilder.fromString(url).getParametersByName("q").get(0).getValue(), "UTF-8");
        }
        return null;
    }

    public String googleQ(String s) {
        s = s.replaceAll("[^0-9a-zA-Zа-яА-ЯёЁ\\s]", "");
        s = s.replaceAll("\\s", "+");
        return "https://www.google.by/search?q=" + s;
    }
}
