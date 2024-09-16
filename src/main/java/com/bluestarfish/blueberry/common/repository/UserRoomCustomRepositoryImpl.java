package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.common.entity.QUserRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRoomCustomRepositoryImpl implements UserRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int countActiveMembersByRoomId(Long roomId) {
        QUserRoom userRoom = QUserRoom.userRoom;

        Long count = jpaQueryFactory
                .select(userRoom.count())
                .from(userRoom)
                .where(userRoom.room.id.eq(roomId)
                        .and(userRoom.isActive.isTrue()))
                .fetchOne();
        return (count != null) ? count.intValue() : 0;
    }
}
