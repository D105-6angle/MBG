package com.ssafy.exception.auth;

// 사용자를 못 찾았을 때의 예외
public class NotFoundUserException extends RuntimeException{
    public NotFoundUserException(String message) {
        super(message);
    }
}
