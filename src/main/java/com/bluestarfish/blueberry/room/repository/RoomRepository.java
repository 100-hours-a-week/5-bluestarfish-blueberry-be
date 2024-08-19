package com.bluestarfish.blueberry.room.repository;

import com.bluestarfish.blueberry.room.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndDeletedAtIsNull(Long id);
    List<Room> findByDeletedAtIsNull();
}
