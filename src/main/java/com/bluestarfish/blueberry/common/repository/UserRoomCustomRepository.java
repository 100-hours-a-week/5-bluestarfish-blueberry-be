package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.user.entity.User;
import java.util.List;

public interface UserRoomCustomRepository {
    List<User> findActiveUsersByRoomId(Long roomId);
    int countActiveMembersByRoomId(Long roomId);
}
