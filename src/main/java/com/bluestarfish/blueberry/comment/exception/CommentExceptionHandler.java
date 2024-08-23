package com.bluestarfish.blueberry.comment.exception;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleFailureResponse;

import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import com.bluestarfish.blueberry.post.exception.PostException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler({PostException.class})
    public ApiFailureResponse<?> handleException(HttpServletRequest request, CommentException e) {
        log.error(e.getMessage());
        return handleFailureResponse(e.getMessage(), e.getHttpStatus());
    }
}
