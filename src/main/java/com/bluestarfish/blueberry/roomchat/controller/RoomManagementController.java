package com.bluestarfish.blueberry.roomchat.controller;

import com.bluestarfish.blueberry.roomchat.dto.RoomManagementDto;
import com.bluestarfish.blueberry.roomchat.service.RoomManagementService;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import java.util.List;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class RoomManagementController {

    private final RoomManagementService roomManagementService;

    public RoomManagementController(RoomManagementService roomManagementService) {
        this.roomManagementService = roomManagementService;
    }

    @MessageMapping("/{roomId}/management")
    @SendTo("/rooms/{roomId}")
    public RoomManagementDto roomControl(@DestinationVariable("roomId") Long roomId,
                                         RoomManagementDto roomManagementDto) {
        return roomManagementService.roomControlUpdate(roomId, roomManagementDto);
    }

    @MessageMapping("/{roomId}/member")
    @SendTo("/rooms/{roomId}")
    public List<UserResponse> roomMember(@DestinationVariable("roomId") Long roomId) {
        return roomManagementService.roomMemberList(roomId);
    }

}
