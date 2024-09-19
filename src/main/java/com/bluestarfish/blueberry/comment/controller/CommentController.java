package com.bluestarfish.blueberry.comment.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.comment.dto.CommentRequest;
import com.bluestarfish.blueberry.comment.service.CommentService;
import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiSuccessResponse<?> registerComment(
            @RequestBody CommentRequest commentRequest,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        return handleSuccessResponse(commentService.createComment(commentRequest, accessToken), HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ApiSuccessResponse<?> getCommentList(
            @PathVariable("postId") Long postId,
            @RequestParam(name = "page") int page
    ) {
        return handleSuccessResponse(commentService.getAllCommentsByPostId(postId, page), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/{commentId}")
    public ApiSuccessResponse<?> deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        commentService.deleteCommentById(postId, commentId, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
