package com.bluestarfish.blueberry.comment.repository;

import com.bluestarfish.blueberry.comment.entity.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndDeletedAtIsNull(Long postId, Pageable pageable);
    Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);
}
