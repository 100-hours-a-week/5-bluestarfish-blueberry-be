package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface RoomService {
    void createRoom(RoomRequest roomRequest);
    RoomResponse getRoomById(Long id);
    Page<RoomResponse> getAllRooms(int page, String keyword, String isCanEnabled);
    void deleteRoomById(Long id);
}
