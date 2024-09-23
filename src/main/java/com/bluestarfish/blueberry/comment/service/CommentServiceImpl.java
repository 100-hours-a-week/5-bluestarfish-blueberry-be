package com.bluestarfish.blueberry.comment.service;

import com.bluestarfish.blueberry.comment.dto.CommentRequest;
import com.bluestarfish.blueberry.comment.dto.CommentResponse;
import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.comment.repository.CommentRepository;
import com.bluestarfish.blueberry.exception.CustomException;
import com.bluestarfish.blueberry.exception.ExceptionDomain;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.repository.PostRepository;
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
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JWTUtils jwtUtils;

    @Override
    public void createComment(CommentRequest commentRequest, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));

        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new CustomException("Post not found with id: " + commentRequest.getPostId(), ExceptionDomain.COMMENT, HttpStatus.NOT_FOUND));
        User user = userRepository.findByIdAndDeletedAtIsNull(commentRequest.getUserId())
                .orElseThrow(() -> new CustomException("User not found with id: " + commentRequest.getUserId(), ExceptionDomain.COMMENT, HttpStatus.NOT_FOUND));

        if(!tokenId.equals(user.getId())) {
            throw new CustomException("Not match request ID and login ID", ExceptionDomain.COMMENT, HttpStatus.UNAUTHORIZED);
        }

        if(commentRequest.getMentionId() != null) {
            User mentionedUser = userRepository.findByIdAndDeletedAtIsNull(commentRequest.getMentionId())
                    .orElseThrow(() -> new CustomException("Mentioned User not found with id: " + commentRequest.getUserId(), ExceptionDomain.COMMENT, HttpStatus.NOT_FOUND));
            commentRepository.save(commentRequest.toEntity(post, user, mentionedUser));
            return;
        }
        commentRepository.save(commentRequest.toEntity(post, user));
    }

    @Override
    public Page<CommentResponse> getAllCommentsByPostId(Long postId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));

        return commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable).map(CommentResponse::from);
    }

    @Override
    public void deleteCommentById(Long postId, Long commentId, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));

        User user = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException("Post not found with id: " + postId, ExceptionDomain.COMMENT, HttpStatus.NOT_FOUND)).getUser();

        if(!tokenId.equals(user.getId())) {
            throw new CustomException("Not match request ID and login ID", ExceptionDomain.COMMENT, HttpStatus.UNAUTHORIZED);
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new CustomException("Comment not found with id: " + commentId, ExceptionDomain.COMMENT, HttpStatus.NOT_FOUND));
        comment.setDeletedAt(LocalDateTime.now());
    }
}
