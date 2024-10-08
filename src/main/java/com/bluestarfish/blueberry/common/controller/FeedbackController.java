package com.bluestarfish.blueberry.common.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.dto.FeedbackRequest;
import com.bluestarfish.blueberry.common.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ApiSuccessResponse<?> sendFeedback(
            @RequestBody FeedbackRequest feedbackRequest
    ) {
        feedbackService.sendFeedback(feedbackRequest);
        return handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping
    public ApiSuccessResponse<?> getFeedbacks() {
        return handleSuccessResponse(feedbackService.getFeedbacks(), HttpStatus.OK);
    }

}
