package com.bluestarfish.blueberry.health.controller;

import com.bluestarfish.blueberry.health.dto.HealthResponse;
import com.bluestarfish.common.dto.ApiSuccessResponse;
import com.bluestarfish.common.handler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiSuccessResponse checkHealth() {
        return ResponseHandler.handleSuccessResponse(new HealthResponse("200 OK"), HttpStatus.OK);
    }

}
