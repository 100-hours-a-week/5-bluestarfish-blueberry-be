package com.bluestarfish.blueberry.common.dto;

import com.bluestarfish.blueberry.common.entity.Feedback;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeedbackResponse {
    private String nickname;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
                .nickname(feedback.getUser() != null && feedback.getUser().getNickname() != null
                        ? feedback.getUser().getNickname()
                        : "익명")
                .content(feedback.getContent())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
