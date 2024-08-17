package com.bluestarfish.blueberry.post.service;

import com.bluestarfish.blueberry.post.dto.PostRequest;
import com.bluestarfish.blueberry.post.dto.PostResponse;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.exception.PostException;
import com.bluestarfish.blueberry.post.repository.PostRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;

    @Override
    public void createPost(PostRequest postRequest) {
        User user = userRepository.findById(postRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not fount with id: " + postRequest.getUserId()));
        Room room = roomRepository.findById(postRequest.getRoomId())
                .orElseThrow(() -> new RoomException("Room not found with id: " + postRequest.getRoomId(), HttpStatus.NOT_FOUND));
        Post post = postRequest.toEntity(user, room);
        postRepository.save(post);
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        return PostResponse.from(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @Override
    public void updatePostById(Long id, PostRequest postRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setPostType(postRequest.getPostType());
        post.setRecruited(postRequest.getIsRecruited());
    }

    @Override
    public void deletePostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        post.setDeletedAt(LocalDateTime.now());
    }
}