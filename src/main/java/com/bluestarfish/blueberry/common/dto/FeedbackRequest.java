package com.bluestarfish.blueberry.common.dto;

import com.bluestarfish.blueberry.common.entity.Feedback;
import com.bluestarfish.blueberry.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {
    private Long userId;
    private String content;

    public Feedback toEntity(User user) {
        return Feedback.builder()
                .user(user)
                .content(content)
                .build();
    }
}
