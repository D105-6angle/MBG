package com.ssafy.exception.schedule;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }

    public RoomNotFoundException(Long roomId) {
        super("Room not found with id: " + roomId);
    }
}