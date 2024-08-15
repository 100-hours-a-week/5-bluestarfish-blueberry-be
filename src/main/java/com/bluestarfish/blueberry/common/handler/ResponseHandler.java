package com.bluestarfish.blueberry.common.handler;


import com.bluestarfish.blueberry.common.dto.ApiFailureResponse;
import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import org.springframework.http.HttpStatus;

public class ResponseHandler {
    public static <T> ApiSuccessResponse<T> handleSuccessResponse(T data, HttpStatus status) {
        return new ApiSuccessResponse<>(data, status);
    }

    public static <T> ApiSuccessResponse<T> handleSuccessResponse(HttpStatus status) {
        return new ApiSuccessResponse<>(status);
    }

    public static <T> ApiFailureResponse<T> handleFailureResponse(T data, HttpStatus status) {
        return new ApiFailureResponse<>(data, status);
    }
}
