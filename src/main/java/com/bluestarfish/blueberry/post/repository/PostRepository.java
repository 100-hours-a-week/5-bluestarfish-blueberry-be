package com.bluestarfish.blueberry.post.repository;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);
    Page<Post> findByIsRecruitedAndDeletedAtIsNull(boolean isRecruited, Pageable pageable);
    Page<Post> findByPostTypeAndIsRecruitedAndDeletedAtIsNull(PostType postType, boolean isRecruited, Pageable pageable);
}
