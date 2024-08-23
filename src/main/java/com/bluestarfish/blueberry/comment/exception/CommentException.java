package com.bluestarfish.blueberry.comment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommentException extends RuntimeException {
    private final HttpStatus httpStatus;

    public CommentException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
