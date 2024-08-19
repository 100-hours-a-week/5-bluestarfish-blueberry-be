package com.bluestarfish.blueberry.common.service;

import com.bluestarfish.blueberry.common.dto.FeedbackRequest;
import com.bluestarfish.blueberry.common.dto.FeedbackResponse;
import com.bluestarfish.blueberry.common.entity.Feedback;
import com.bluestarfish.blueberry.common.exception.FeedbackException;
import com.bluestarfish.blueberry.common.repository.FeedbackRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public void sendFeedback(FeedbackRequest feedbackRequest) {
        User user = Optional.ofNullable(feedbackRequest.getUserId())
                .flatMap(userRepository::findById)
                .orElse(null);

        Feedback feedback = feedbackRequest.toEntity(user);
        feedbackRepository.save(feedback);
    }

    public List<FeedbackResponse> getFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        if (feedbacks.isEmpty()) {
            throw new FeedbackException("Feedbacks Not found", HttpStatus.NOT_FOUND); // 예외 발생
        }

        return feedbacks.stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

}
