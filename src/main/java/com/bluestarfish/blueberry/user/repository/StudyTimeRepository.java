package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyTimeRepository extends JpaRepository<StudyTime, Long>, CustomStudyTimeRepository {
    Optional<StudyTime> findByUserId(Long userId);
}
