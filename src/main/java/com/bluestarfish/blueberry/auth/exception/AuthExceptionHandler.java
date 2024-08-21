package com.bluestarfish.blueberry.auth.exception;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleFailureResponse;

import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ApiFailureResponse<?> handleException(HttpServletRequest request, AuthException e) {
        log.error(e.getMessage());

        return handleFailureResponse(e.getMessage(), e.getHttpStatus());
    }
}
