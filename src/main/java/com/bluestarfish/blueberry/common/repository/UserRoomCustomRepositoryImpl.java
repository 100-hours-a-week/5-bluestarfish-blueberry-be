package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.common.entity.QUserRoom;
import com.bluestarfish.blueberry.user.entity.QUser;
import com.bluestarfish.blueberry.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRoomCustomRepositoryImpl implements UserRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findActiveUsersByRoomId(Long roomId) {
        QUserRoom userRoom = QUserRoom.userRoom;
        QUser user = QUser.user;

        return jpaQueryFactory
                .select(user)
                .from(userRoom)
                .join(userRoom.user, user)
                .where(userRoom.room.id.eq(roomId)
                        .and(userRoom.isActive.isTrue()))
                .fetch();
    }

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
