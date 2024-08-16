package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import java.util.List;

public interface RoomService {
    void createRoom(RoomRequest roomRequest);
    RoomResponse getRoomById(Long id);
    List<RoomResponse> getAllRooms();
    void updateRoomById(Long id, RoomRequest roomRequest);
    void deleteRoomById(Long id);
}
