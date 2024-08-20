package com.bluestarfish.blueberry.comment.dto;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponse {
    private Long id;
    private Long postId;
    private User user;
    private User mentionedUser;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .user(comment.getUser())
                .mentionedUser(comment.getMetionedUser())
                .content(comment.getContent())
                .build();
    }
}
