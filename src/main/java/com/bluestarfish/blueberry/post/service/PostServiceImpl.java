package com.bluestarfish.blueberry.post.service;

import com.bluestarfish.blueberry.post.dto.PostRequest;
import com.bluestarfish.blueberry.post.dto.PostResponse;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.post.exception.PostException;
import com.bluestarfish.blueberry.post.repository.PostRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public void createPost(PostRequest postRequest) {
        User user = userRepository.findByIdAndDeletedAtIsNull(postRequest.getUserId())
                .orElseThrow(() -> new PostException("User not fount with id: " + postRequest.getUserId(), HttpStatus.NOT_FOUND));
        Post post;

        if(postRequest.getRoomId() != null) {
            Room room = roomRepository.findByIdAndDeletedAtIsNull(postRequest.getRoomId())
                    .orElseThrow(() -> new PostException("Room not found with id: " + postRequest.getRoomId(), HttpStatus.NOT_FOUND));
            post = postRequest.toEntity(user, room);
        } else {
            post = postRequest.toEntity(user);
        }
        postRepository.save(post);
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PostException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        return PostResponse.from(post);
    }

    @Override
    public Page<PostResponse> getAllPosts(
            int page,
            PostType postType,
            boolean isRecruited
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));

        if(postType == null) {
            return postRepository.findByIsRecruitedAndDeletedAtIsNull(isRecruited, pageable).map(PostResponse::from);
        } else {
            return postRepository.findByPostTypeAndIsRecruitedAndDeletedAtIsNull(postType, isRecruited, pageable).map(PostResponse::from);
        }
    }

    @Override
    public void updatePostById(Long id, PostRequest postRequest) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setPostType(postRequest.getPostType());
        post.setRecruited(postRequest.getIsRecruited());
    }

    @Override
    public void deletePostById(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        post.setDeletedAt(LocalDateTime.now());
    }
}