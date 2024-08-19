package com.bluestarfish.blueberry.common.service;

import com.bluestarfish.blueberry.common.dto.FeedbackRequest;

public interface FeedbackService {

    void sendFeedback(FeedbackRequest feedbackRequest);

}
