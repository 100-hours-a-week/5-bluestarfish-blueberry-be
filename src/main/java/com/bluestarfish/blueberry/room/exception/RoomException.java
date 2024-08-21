package com.bluestarfish.blueberry.room.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoomException extends RuntimeException {
    private final HttpStatus httpStatus;

    public RoomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
