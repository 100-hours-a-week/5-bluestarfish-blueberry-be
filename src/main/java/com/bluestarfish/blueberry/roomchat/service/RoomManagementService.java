package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.RoomManagementDto;

public interface RoomManagementService {

    RoomManagementDto roomControlUpdate(Long roomId, RoomManagementDto roomManagementDto);
}
