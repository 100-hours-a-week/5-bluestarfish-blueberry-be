package com.bluestarfish.common.dto;


import com.bluestarfish.common.dto.ApiSuccessResponse.CommonSuccessData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ApiSuccessResponse<T> extends ResponseEntity<CommonSuccessData<T>> {
    public ApiSuccessResponse(T data, HttpStatus status) {
        super(new CommonSuccessData<>(data), status);
    }

    public ApiSuccessResponse(HttpStatus status) {
        super(status);
    }

    public record CommonSuccessData<T>(
            @JsonProperty("data") T data
    ) {
    }
}
