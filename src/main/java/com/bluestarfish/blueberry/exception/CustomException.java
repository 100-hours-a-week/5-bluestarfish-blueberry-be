package com.bluestarfish.blueberry.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final ExceptionDomain exceptionDomain;
    private final HttpStatus httpStatus;

    public CustomException(
            String message,
            ExceptionDomain exceptionDomain,
            HttpStatus httpStatus
    ) {
        super("ERROR DOMAIN: " + exceptionDomain.getDomain() + ", ERROR MESSAGE: " + message);
        this.exceptionDomain = exceptionDomain;
        this.httpStatus = httpStatus;
    }
}
