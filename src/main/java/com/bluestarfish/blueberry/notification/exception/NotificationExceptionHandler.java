package com.bluestarfish.blueberry.notification.exception;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleFailureResponse;

import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ApiFailureResponse<?> handleException(HttpServletRequest request, NotificationException e) {
        log.error(e.getMessage());

        return handleFailureResponse(e.getMessage(), e.getHttpStatus());
    }
}
