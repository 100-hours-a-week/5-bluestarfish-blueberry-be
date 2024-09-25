package com.bluestarfish.blueberry.exception;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleFailureResponse;

import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ApiFailureResponse<?> handleException(HttpServletRequest request, CustomException e) {
        String message = "Error Domain: " + e.getExceptionDomain().getDomain() + ", Error message: " + e.getMessage();
        log.error(message);
        Sentry.captureException(e);
        return handleFailureResponse(message, e.getHttpStatus());
    }
}
