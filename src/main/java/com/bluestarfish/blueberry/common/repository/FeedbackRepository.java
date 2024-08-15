package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.common.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
