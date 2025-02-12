package com.ssafy.exception.mypage;

public class NotFoundCardException extends RuntimeException {
    public NotFoundCardException(String message) {
        super(message);
    }
}
