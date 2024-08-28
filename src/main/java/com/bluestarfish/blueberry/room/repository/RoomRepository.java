package com.bluestarfish.blueberry.room.repository;

import com.bluestarfish.blueberry.room.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndDeletedAtIsNull(Long id);
    Page<Room> findByDeletedAtIsNull(Pageable pageable);
    Page<Room> findByIsCamEnabledAndDeletedAtIsNull(boolean isCamEnabled, Pageable pageable);
    Page<Room> findByTitleContainingAndDeletedAtIsNull(String keyword, Pageable pageable);
    Page<Room> findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(String keyword, boolean isCamEnabled, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.id IN ("
            + "SELECT ur.room.id FROM UserRoom ur "
            + "WHERE ur.user.id = :userId "
            + "AND ur.isHost = true"
            + ")"
            + "AND r.deletedAt is NULL")
    List<Room> findRoomsByUserIdAndIsHost(@Param("userId") Long userId);
}
