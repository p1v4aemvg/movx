package com.by.movx.event;

public abstract class Event<T> {

    private T data;

    public Event(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
