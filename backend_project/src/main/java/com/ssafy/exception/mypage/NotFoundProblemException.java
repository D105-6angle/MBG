package com.ssafy.exception.mypage;

public class NotFoundProblemException extends RuntimeException {
    public NotFoundProblemException(String message) {
        super(message);
    }
}
