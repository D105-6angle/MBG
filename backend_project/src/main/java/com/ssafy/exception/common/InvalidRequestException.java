package com.ssafy.exception.common;

public class InvalidRequestException extends RuntimeException  {
    public InvalidRequestException(String message) {
        super(message);
    }
}
