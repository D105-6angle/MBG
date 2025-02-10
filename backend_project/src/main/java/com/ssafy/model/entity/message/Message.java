package com.ssafy.model.entity.message;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private Notification notification;
    private String token;
    private Map<String, String> data;

    // 기본 생성자
    public Message() {
        this.data = new HashMap<>();
    }

    public Message(Notification notification, String token) {
        this.notification = notification;
        this.token = token;
        this.data = new HashMap<>();
    }

    public Message putAllData(Map<String, String> data) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.putAll(data);
        return this;
    }

    // getter, setter 메소드들...
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
