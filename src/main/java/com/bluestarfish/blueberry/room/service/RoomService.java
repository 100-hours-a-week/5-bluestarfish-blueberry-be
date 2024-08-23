package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.common.dto.UserRoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomDetailResponse;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import org.springframework.data.domain.Page;

public interface RoomService {
    void createRoom(RoomRequest roomRequest, Long userId);
    RoomDetailResponse getRoomById(Long id);
    Page<RoomResponse> getAllRooms(int page, String keyword, Boolean isCanEnabled);
    void deleteRoomById(Long id);
    void entranceRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest);
    void exitRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest);
}
