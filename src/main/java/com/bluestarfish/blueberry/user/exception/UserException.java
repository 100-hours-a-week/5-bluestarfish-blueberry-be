package com.bluestarfish.blueberry.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private final HttpStatus httpStatus;

    public UserException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
