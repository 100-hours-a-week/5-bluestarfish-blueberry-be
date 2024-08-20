package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.RoomManagementDto;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import java.util.List;

public interface RoomManagementService {

    RoomManagementDto roomControlUpdate(Long roomId, RoomManagementDto roomManagementDto);

    List<UserResponse> roomMemberList(Long roomId);
}
