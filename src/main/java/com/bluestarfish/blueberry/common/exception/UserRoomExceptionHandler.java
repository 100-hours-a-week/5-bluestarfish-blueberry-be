package com.bluestarfish.blueberry.common.exception;

import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import com.bluestarfish.blueberry.common.handler.ResponseHandler;
import com.bluestarfish.blueberry.room.exception.RoomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserRoomExceptionHandler {
    @ExceptionHandler({RoomException.class})
    public ApiFailureResponse<?> handleException(HttpServletRequest request, RoomException e) {
        log.error(e.getMessage());

        return ResponseHandler.handleFailureResponse(e.getMessage(), e.getHttpStatus());
    }
}
