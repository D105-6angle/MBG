package com.ssafy.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class Alarm {
    private Long alarmId;
    private Long userId;
    private String title;
    private String content;
    private String sentTime;

    public Alarm() {}

    public Alarm(Long alarmId, Long userId, String title, String content, String sentTime) {
        this.alarmId = alarmId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.sentTime = sentTime;
    }

    public Alarm(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Alarm [alarmId=" + alarmId + ", userId=" + userId + ", title=" + title + ", content=" + content + ", sentTime=" + sentTime + "]";
    }
}