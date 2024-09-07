package com.bluestarfish.blueberry.room.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.dto.UserRoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomPasswordRequest;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

@RestController
@RequestMapping(value = "/api/v1/rooms", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping(consumes = "multipart/form-data")
    public ApiSuccessResponse<?> registerStudyRoom(
            @ModelAttribute RoomRequest roomRequest
            @CookieValue(name = "Authorization") String accessToken
    ) {
        roomService.createRoom(roomRequest, accessToken);
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
            @RequestParam(name = "isCamEnabled", required = false) Boolean isCamEnabled
    ) {
        return handleSuccessResponse(roomService.getAllRooms(page, keyword, isCamEnabled), HttpStatus.OK);
    }

    @GetMapping("/my/{userId}")
    public ApiSuccessResponse<?> getMyRoomList(
            @PathVariable("userId") Long userId
    ) {
        return handleSuccessResponse(roomService.getMyRooms(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ApiSuccessResponse<?> deleteStudyRoom(
            @PathVariable("roomId") Long id,
            @CookieValue(name = "Authorization") String accessToken
    ) {
        roomService.deleteRoomById(id, accessToken);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{roomId}/users/{userId}")
    public ApiSuccessResponse<?> entranceStudyRoom(
            @PathVariable("roomId") Long roomId,
            @PathVariable("userId") Long userId,
            @RequestBody UserRoomRequest userRoomRequest
    ) {
        if (userRoomRequest.isActive()) { // 입장 or 재입장 요청
            roomService.entranceRoom(roomId, userId, userRoomRequest);
        } else { // 퇴장 요청
            roomService.exitRoom(roomId, userId, userRoomRequest);
        }
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/password")
    public ApiSuccessResponse<?> checkRoomPassword(
            @RequestBody RoomPasswordRequest roomPasswordRequest
    ) {
        roomService.checkRoomPassword(roomPasswordRequest);
        return handleSuccessResponse(HttpStatus.OK);
    }
}
