package com.bluestarfish.blueberry.room.repository;

import com.bluestarfish.blueberry.room.entity.Room;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndDeletedAtIsNull(Long id);
    Page<Room> findByDeletedAtIsNull(Pageable pageable);
    Page<Room> findByIsCamEnabledAndDeletedAtIsNull(boolean isCamEnabled, Pageable pageable);
    Page<Room> findByTitleContainingAndDeletedAtIsNull(String keyword, Pageable pageable);
    Page<Room> findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(String keyword, boolean isCamEnabled, Pageable pageable);
}
