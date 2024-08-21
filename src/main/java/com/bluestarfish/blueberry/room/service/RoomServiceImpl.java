package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public void createRoom(RoomRequest roomRequest) {
        roomRepository.save(roomRequest.toEntity());
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        return RoomResponse.from(room);
    }

    @Override
    public Page<RoomResponse> getAllRooms(int page, String keyword, Boolean isCamEnabled) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));

        // 이후 QueryDSL or @Query 스타일로 변경 검토
        if(keyword == null && isCamEnabled == null) { // 검색 keyword가 없고, 캠 여부가 전체 인 경우 조회
            return roomRepository.findByDeletedAtIsNull(pageable).map(RoomResponse::from);
        } else if(keyword == null) { // 검색어가 없고, 캠여부가 true or false 인 경우 조회
            if(isCamEnabled) {
                return roomRepository.findByIsCamEnabledAndDeletedAtIsNull(true, pageable).map(RoomResponse::from);
            } else {
                return roomRepository.findByIsCamEnabledAndDeletedAtIsNull(false, pageable).map(RoomResponse::from);
            }
        } else if(isCamEnabled == null) { // 검색어가 있고, 캠 여부가 all 인 경우 조회
            return roomRepository.findByTitleContainingAndDeletedAtIsNull(keyword, pageable).map(RoomResponse::from);
        } else { // 검색어가 있고, 캠 여부가 true or false 인 경우 조회
            if(isCamEnabled) {
                return roomRepository.findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(keyword, true, pageable).map(RoomResponse::from);
            } else {
                return roomRepository.findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(keyword, false, pageable).map(RoomResponse::from);
            }
        }
    }

    @Override
    public void deleteRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        room.setDeletedAt(LocalDateTime.now());
    }
}
