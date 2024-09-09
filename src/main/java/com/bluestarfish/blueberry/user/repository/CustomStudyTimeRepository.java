package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.StudyTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomStudyTimeRepository {
    Optional<StudyTime> findByUserIdAndToday(Long userId);

    List<StudyTime> findRanksTop10Yesterday();

    List<StudyTime> findRanksYesterday(Long userId);

    List<StudyTime> findStudyTimesBetweenDates(Long userId, LocalDate startDate, LocalDate endDate);
}
