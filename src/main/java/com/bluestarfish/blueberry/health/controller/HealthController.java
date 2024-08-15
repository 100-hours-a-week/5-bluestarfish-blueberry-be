package com.bluestarfish.blueberry.health.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.health.dto.HealthResponse;
import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiSuccessResponse<?> checkHealth() {
        return handleSuccessResponse(new HealthResponse("200 OK"), HttpStatus.OK);

    }

}
