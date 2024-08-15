package com.bluestarfish.blueberry.post.repository;

import com.bluestarfish.blueberry.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
