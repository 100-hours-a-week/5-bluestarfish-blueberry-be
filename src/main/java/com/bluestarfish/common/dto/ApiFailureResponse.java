package com.bluestarfish.common.dto;


import com.bluestarfish.common.dto.ApiFailureResponse.CommonFailureData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ApiFailureResponse <T> extends ResponseEntity<CommonFailureData<T>> {
    public ApiFailureResponse(T data, HttpStatus status) {
        super(new CommonFailureData<>(data), status);
    }

    public record CommonFailureData<T>(
            @JsonProperty("message") T data
    ) {
    }
}
