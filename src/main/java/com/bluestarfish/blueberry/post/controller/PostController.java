package com.bluestarfish.blueberry.post.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.post.dto.PostRequest;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ApiSuccessResponse<?> registerPost(
            @Valid @RequestBody PostRequest postRequest,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        postService.createPost(postRequest, accessToken);
        return handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ApiSuccessResponse<?> getPost(
            @PathVariable("postId") Long id
    ) {
        return handleSuccessResponse(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping
    public ApiSuccessResponse<?> getPostList(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "type", required = false) PostType postType,
            @RequestParam(name = "recruited", required = false) boolean isRecruited
    ) {
        return handleSuccessResponse(postService.getAllPosts(page, postType, isRecruited), HttpStatus.OK);
    }

    @PatchMapping("/{postId}")
    public ApiSuccessResponse<?> updatePost(
            @PathVariable("postId") Long id,
            @RequestBody PostRequest postRequest,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        postService.updatePostById(id, postRequest, accessToken);
        return handleSuccessResponse(HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ApiSuccessResponse<?> deletePost(
            @PathVariable("postId") Long id,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        postService.deletePostById(id, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
