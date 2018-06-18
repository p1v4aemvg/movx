package com.by.movx.event;

import com.by.movx.entity.CustomQuery;

/**
 * Created by Администратор
 * on 18.06.2018.
 */
public class StatNodeClickedEvent extends Event<String> {
    private CustomQuery query;

    public StatNodeClickedEvent(CustomQuery query, String data) {
        super(data);
        this.query = query;
    }

    public CustomQuery getQuery() {
        return query;
    }
}
