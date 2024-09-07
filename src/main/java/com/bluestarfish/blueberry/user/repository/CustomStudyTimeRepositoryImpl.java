package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.QStudyTime;
import com.bluestarfish.blueberry.user.entity.StudyTime;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomStudyTimeRepositoryImpl implements CustomStudyTimeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<StudyTime> findByUserIdAndToday(Long userId) {
        QStudyTime qStudyTime = QStudyTime.studyTime;

        System.out.println("asd " + LocalDate.now());


        StudyTime studyTime = queryFactory.selectFrom(qStudyTime)
                .where(
                        qStudyTime.date.eq(LocalDate.now()) // 객체비교? 값이아니라?
                                .and(qStudyTime.user.id.eq(userId))
                )
                .fetchOne();

        return Optional.ofNullable(studyTime);
    }
}
