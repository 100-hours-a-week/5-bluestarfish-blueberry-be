package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findByDeletedAtIsNull().stream()
                .map(RoomResponse::from)
                .toList();
    }

    @Override
    public void updateRoomById(Long id, RoomRequest roomRequest) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));

        room.setTitle(roomRequest.getTitle());
        room.setMaxUsers(roomRequest.getMaxUsers());
        room.setCamEnabled(roomRequest.isCamEnabled());
        room.setPassword(roomRequest.getPassword());
        room.setThumbnail(roomRequest.getThumbnail());
        room.setDescription(roomRequest.getDescription());
    }

    @Override
    public void deleteRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        room.setDeletedAt(LocalDateTime.now());
    }
}
