package com.bluestarfish.blueberry.post.repository;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByPostTypeAndIsRecruited(PostType postType, boolean isRecruited, Pageable pageable);
}
