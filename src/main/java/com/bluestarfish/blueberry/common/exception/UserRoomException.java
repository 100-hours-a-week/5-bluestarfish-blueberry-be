package com.bluestarfish.blueberry.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserRoomException extends RuntimeException {
    private final HttpStatus httpStatus;

    public UserRoomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
