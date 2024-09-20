package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.notification.entity.QNotification;
import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import com.bluestarfish.blueberry.user.dto.FoundUserResponse;
import com.bluestarfish.blueberry.user.entity.QStudyTime;
import com.bluestarfish.blueberry.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FoundUserResponse> findUsersByNickname(Long requestUserId, String keyword) {
        QUser qUser = QUser.user;
        QStudyTime qStudyTime = QStudyTime.studyTime;
        QNotification qNotification = QNotification.notification;

        LocalDate yesterday = LocalDate.now().minusDays(1);

        return queryFactory.select(Projections.constructor(FoundUserResponse.class,
                        qUser.id,
                        qUser.profileImage,
                        qUser.nickname,
                        qStudyTime.time.coalesce(Time.valueOf("00:00:00")),
                        qNotification.id.isNotNull()
                ))
                .from(qUser)
                .leftJoin(qStudyTime).on(qUser.id.eq(qStudyTime.user.id)
                        .and(qStudyTime.date.eq(yesterday)))
                .leftJoin(qNotification).on(
                        qNotification.sender.id.eq(requestUserId)
                                .and(qNotification.receiver.id.eq(qUser.id))
                                .and(qNotification.notiType.eq(NotiType.FRIEND))
                                .and(qNotification.notiStatus.eq(NotiStatus.ACCEPTED))
                                .or(
                                        qNotification.sender.id.eq(qUser.id)
                                                .and(qNotification.receiver.id.eq(requestUserId))
                                                .and(qNotification.notiType.eq(NotiType.FRIEND))
                                                .and(qNotification.notiStatus.eq(NotiStatus.ACCEPTED))
                                )
                )
                .where(qUser.nickname.contains(keyword))
                .fetch();
    }
}
