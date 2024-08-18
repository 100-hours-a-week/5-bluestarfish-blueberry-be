package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

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
    public Page<RoomResponse> getAllRooms(int page, String keyword, boolean isCamEnabled) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));
        Page<Room> roomPage = roomRepository.findByKeywordAndIsCamEnabled(keyword, isCamEnabled, pageable);

        List<RoomResponse> roomResponses = roomPage.getContent().stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(roomResponses, pageable, roomPage.getTotalElements());
    }

    @Override
    public void deleteRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        room.setDeletedAt(LocalDateTime.now());
    }
}
