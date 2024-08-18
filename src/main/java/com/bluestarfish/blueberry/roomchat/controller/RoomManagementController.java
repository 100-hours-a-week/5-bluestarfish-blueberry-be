package com.bluestarfish.blueberry.roomchat.controller;

import com.bluestarfish.blueberry.roomchat.service.RoomManagementService;
import org.springframework.stereotype.Controller;

@Controller
public class RoomManagementController {

    private final RoomManagementService roomManagementService;

    public RoomManagementController(RoomManagementService roomManagementService) {
        this.roomManagementService = roomManagementService;
    }

//    @MessageMapping("/{roomId}/management")
//    @SendTo("/rooms/{roomId}")
//    public UserRoom roomControl(@DestinationVariable("roomId") Long roomId, RoomManagement roomManagement) {
//        UserRoom userRoom = roomManagementService.roomControlUpdate(roomId, roomManagement);
//        return userRoom;
//    }
}
