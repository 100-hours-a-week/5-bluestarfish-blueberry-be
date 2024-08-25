package com.bluestarfish.blueberry.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeedbackException extends RuntimeException {
    private final HttpStatus httpStatus;

    public FeedbackException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
