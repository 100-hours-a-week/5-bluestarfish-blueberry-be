package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.common.dto.UserRoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomDetailResponse;
import com.bluestarfish.blueberry.room.dto.RoomPasswordRequest;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface RoomService {
    void createRoom(RoomRequest roomRequest);
    RoomDetailResponse getRoomById(Long id);
    Page<RoomResponse> getAllRooms(int page, String keyword, Boolean isCanEnabled);
    List<RoomResponse> getMyRooms(Long userId);
    void deleteRoomById(Long id);
    void entranceRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest);
    void exitRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest);
    int getActiveMemberCount(Long roomId);
    void checkRoomPassword(RoomPasswordRequest roomPasswordRequest);
}
