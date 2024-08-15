package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.common.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
}
