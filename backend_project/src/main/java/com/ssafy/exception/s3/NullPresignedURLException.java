package com.ssafy.exception.s3;

public class NullPresignedURLException extends RuntimeException {
    public NullPresignedURLException(String message) {
        super(message);
    }
}
