package com.bluestarfish.blueberry.common.service;

import com.bluestarfish.blueberry.common.dto.FeedbackRequest;
import com.bluestarfish.blueberry.common.dto.FeedbackResponse;
import com.bluestarfish.blueberry.common.entity.Feedback;
import com.bluestarfish.blueberry.common.repository.FeedbackRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public void sendFeedback(FeedbackRequest feedbackRequest) {
        User user = userRepository.findById(feedbackRequest.getUserId()).orElse(null);
        feedbackRepository.save(feedbackRequest.toEntity(user));
    }

    public List<FeedbackResponse> getFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

}
