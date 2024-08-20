package com.bluestarfish.blueberry.post.service;


import com.bluestarfish.blueberry.post.dto.PostRequest;
import com.bluestarfish.blueberry.post.dto.PostResponse;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import org.springframework.data.domain.Page;

public interface PostService {
    void createPost(PostRequest postRequest);
    PostResponse getPostById(Long id);
    Page<PostResponse> getAllPosts(int page, PostType postType, boolean isRecruited);
    void updatePostById(Long id, PostRequest postRequest);
    void deletePostById(Long id);
}
