package com.ssafy.controller.fcm;

import java.time.LocalDateTime;

public class FcmDto {
    private Long tokenId;    // 추가
    private Long userId;
    private String fcmToken;

    public FcmDto() {}

    public FcmDto(Long userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "Fcm [userId=" + userId + ", fcmToken=" + fcmToken + "]";
    }
}