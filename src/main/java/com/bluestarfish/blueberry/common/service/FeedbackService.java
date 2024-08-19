package com.bluestarfish.blueberry.common.service;

import com.bluestarfish.blueberry.common.dto.FeedbackRequest;
import com.bluestarfish.blueberry.common.dto.FeedbackResponse;
import java.util.List;

public interface FeedbackService {

    void sendFeedback(FeedbackRequest feedbackRequest);
    List<FeedbackResponse> getFeedbacks();
}
