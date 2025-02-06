package com.ssafy.exception.schedule;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }

    public RoomNotFoundException(Long roomId) {
        super("Room not found with id: " + roomId);
    }
}