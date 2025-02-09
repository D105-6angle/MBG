package com.ssafy.exception.auth;

public class WithdrawnUserException extends RuntimeException {
    public WithdrawnUserException(String message) {
        super(message);
    }
}
