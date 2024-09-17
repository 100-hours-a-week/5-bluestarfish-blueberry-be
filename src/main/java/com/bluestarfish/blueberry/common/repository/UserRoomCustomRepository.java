package com.bluestarfish.blueberry.common.repository;

public interface UserRoomCustomRepository {
    int countActiveMembersByRoomId(Long roomId);
}
