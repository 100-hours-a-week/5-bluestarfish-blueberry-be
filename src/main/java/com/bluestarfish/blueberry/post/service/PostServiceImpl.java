package com.bluestarfish.blueberry.post.service;

import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.post.dto.PostRequest;
import com.bluestarfish.blueberry.post.dto.PostResponse;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.post.exception.PostException;
import com.bluestarfish.blueberry.post.repository.PostRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.room.service.RoomService;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private final RoomService roomService;
    private final JWTUtils jwtUtils;

    @Override
    public void createPost(PostRequest postRequest, String accessToken) {
        User user = userRepository.findByIdAndDeletedAtIsNull(postRequest.getUserId())
                .orElseThrow(() -> new PostException("User not fount with id: " + postRequest.getUserId(), HttpStatus.NOT_FOUND));
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        if(!tokenId.equals(user.getId())) {
            throw new PostException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }

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
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        if(post.getRoom() != null) {
            return PostResponse.from(post, roomService.getActiveMemberCount(post.getRoom().getId()));
        }
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
            if(isRecruited) {
                return postRepository.findByIsRecruitedTrueAndDeletedAtIsNull(pageable).map(post -> {
                    if(post.getRoom() != null) {
                        return PostResponse.from(post, roomService.getActiveMemberCount(post.getRoom().getId()));
                    } else {
                        return PostResponse.from(post);
                    }
                });
            } else {
                return postRepository.findByDeletedAtIsNull(pageable).map(post -> {
                    if(post.getRoom() != null) {
                        return PostResponse.from(post, roomService.getActiveMemberCount(post.getRoom().getId()));
                    } else {
                        return PostResponse.from(post);
                    }
                });
            }
        } else {
            if(isRecruited) {
                return postRepository.findByPostTypeAndIsRecruitedTrueAndDeletedAtIsNull(postType, pageable).map(post -> {
                    if(post.getRoom() != null) {
                        return PostResponse.from(post, roomService.getActiveMemberCount(post.getRoom().getId()));
                    } else {
                        return PostResponse.from(post);
                    }
                });
            } else {
                return postRepository.findByPostTypeAndDeletedAtIsNull(postType, pageable).map(post -> {
                    if(post.getRoom() != null) {
                        return PostResponse.from(post, roomService.getActiveMemberCount(post.getRoom().getId()));
                    } else {
                        return PostResponse.from(post);
                    }
                });
            }
        }
    }

    @Override
    public void updatePostById(Long id, PostRequest postRequest, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));

        if(!tokenId.equals(postRequest.getUserId())) {
            throw new PostException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));
        if(postRequest.getRoomId() != null) {
            Room room = roomRepository.findByIdAndDeletedAtIsNull(postRequest.getRoomId())
                    .orElseThrow(() -> new PostException("room not found with id: " + postRequest.getRoomId(), HttpStatus.NOT_FOUND));
            post.setRoom(room);
        }
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setPostType(postRequest.getType());
        post.setRecruited(postRequest.getIsRecruited());
        post.setPostCamEnabled(postRequest.isPostCamEnabled());
    }

    @Override
    public void deletePostById(Long id, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PostException("Post not found with id: " + id, HttpStatus.NOT_FOUND));

        if(!tokenId.equals(post.getUser().getId())) {
            throw new PostException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }
        post.setDeletedAt(LocalDateTime.now());
    }
}