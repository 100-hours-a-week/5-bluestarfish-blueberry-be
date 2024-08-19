package com.bluestarfish.blueberry.room.controller;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/rooms", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiSuccessResponse<?> registerStudyRoom(
            @RequestBody RoomRequest roomRequest
    ) {
            roomService.createRoom(roomRequest);
            return handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping("/{roomId}")
    public ApiSuccessResponse<?> getStudyRoom(
            @PathVariable("roomId") Long id
    ) {
        return handleSuccessResponse(roomService.getRoomById(id), HttpStatus.OK);
    }

    @GetMapping()
    public ApiSuccessResponse<?> getStudyRoomList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "isCamEnabled", required = false) String isCamEnabled
            ) {
        return handleSuccessResponse(roomService.getAllRooms(page, keyword, isCamEnabled), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ApiSuccessResponse<?> deleteStudyRoom(
            @PathVariable("roomId") Long id
    ) {
        roomService.deleteRoomById(id);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
