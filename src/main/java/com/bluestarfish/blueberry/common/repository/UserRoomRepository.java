package com.bluestarfish.blueberry.common.repository;

import com.bluestarfish.blueberry.common.entity.UserRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    Optional<Object> findByRoomIdAndUserId(Long roomId, Long userId);
}
