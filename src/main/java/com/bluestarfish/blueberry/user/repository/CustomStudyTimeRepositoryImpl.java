package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.QStudyTime;
import com.bluestarfish.blueberry.user.entity.StudyTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomStudyTimeRepositoryImpl implements CustomStudyTimeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<StudyTime> findByUserIdAndToday(Long userId) {
        QStudyTime qStudyTime = QStudyTime.studyTime;

        Date today = new Date();

        StudyTime studyTime = queryFactory.selectFrom(qStudyTime)
                .where(
                        qStudyTime.date.eq(today)
                                .and(qStudyTime.user.id.eq(userId))
                )
                .fetchOne();

        return Optional.ofNullable(studyTime);
    }
}
