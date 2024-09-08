package com.bluestarfish.blueberry.comment.service;

import com.bluestarfish.blueberry.comment.dto.CommentRequest;
import com.bluestarfish.blueberry.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;

public interface CommentService {
    void createComment(CommentRequest commentRequest, String accessToken);
    Page<CommentResponse> getAllCommentsByPostId(Long postId, int page);
    void deleteCommentById(Long postId, Long commentId, String accessToken);
}
