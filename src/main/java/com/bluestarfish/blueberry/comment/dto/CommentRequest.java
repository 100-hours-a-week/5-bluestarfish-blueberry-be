package com.bluestarfish.blueberry.comment.dto;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private Long id;
    private Long postId;
    private Long userId;
    private Long mentionId;
    private String content;

    public Comment toEntity(Post post, User user, User mentionedUser) {
        return Comment.builder()
                .post(post)
                .user(user)
                .mentionedUser(mentionedUser)
                .content(content)
                .build();
    }

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();
    }
}
