package com.bluestarfish.blueberry.comment.service;

import com.bluestarfish.blueberry.comment.dto.CommentRequest;
import com.bluestarfish.blueberry.comment.dto.CommentResponse;
import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.comment.exception.CommentException;
import com.bluestarfish.blueberry.comment.repository.CommentRepository;
import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.repository.PostRepository;
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
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void createComment(CommentRequest commentRequest) {
        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new CommentException("Post not found with id: " + commentRequest.getPostId(), HttpStatus.NOT_FOUND));
        User user = userRepository.findByIdAndDeletedAtIsNull(commentRequest.getUserId())
                .orElseThrow(() -> new CommentException("User not found with id: " + commentRequest.getUserId(), HttpStatus.NOT_FOUND));
        User mentionedUser = userRepository.findByIdAndDeletedAtIsNull(commentRequest.getMentionId())
                .orElseThrow(() -> new CommentException("Mentioned User not found with id: " + commentRequest.getUserId(), HttpStatus.NOT_FOUND));;
        Comment comment = commentRequest.toEntity(post, user, mentionedUser);
        commentRepository.save(comment);
    }

    @Override
    public Page<CommentResponse> getAllCommentsByPostId(Long postId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));

        return commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable).map(CommentResponse::from);
    }

    @Override
    public void deleteCommentById(Long postId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new CommentException("Comment not found with id: " + commentId, HttpStatus.NOT_FOUND));
        comment.setDeletedAt(LocalDateTime.now());
    }
}
