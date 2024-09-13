package com.bluestarfish.blueberry.notification.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.handler.ResponseHandler;
import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.service.NoticeService;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/users")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // SSE 구독 요청 처리: 사용자가 알림을 구독할 때 호출되는 엔드포인트
    @GetMapping("/notification/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable(value = "userId") Long userId) {

        return noticeService.subscribe(userId);
    }

    // 알림 전송 처리
    @PostMapping(value = "/{userId}/notifications")
    public ApiSuccessResponse<?> sendInviteNotification(@PathVariable(value = "userId") Long userId,
                                                        @RequestBody NoticeDto noticeDto) {

        Notification notificationInfo = noticeService.sendNotice(userId, noticeDto);

        return ResponseHandler.handleSuccessResponse(notificationInfo, HttpStatus.OK);
    }

    // 알림 수락,거절 처리
    @PatchMapping("/{userId}/notifications/{notificationId}")
    public ApiSuccessResponse<?> updateNotice(@PathVariable(value = "userId") Long userId,
                                              @PathVariable(value = "notificationId") Long noticeId,
                                              @RequestBody NoticeDto noticeDto) {
        Notification notificationInfo = noticeService.updateNotificationStatus(userId, noticeId, noticeDto);
        return ResponseHandler.handleSuccessResponse(notificationInfo, HttpStatus.OK);
    }

    @GetMapping("/{userId}/notifications")
    public ApiSuccessResponse<?> getNotifications(@PathVariable(value = "userId") Long userId) {
        List<NoticeDto> notifications = noticeService.listNotifications(userId);
        return ResponseHandler.handleSuccessResponse(notifications, HttpStatus.OK);
    }

    //알림 삭제 처리
    @DeleteMapping("/notifications/{notificationId}")
    public ApiSuccessResponse<?> deleteNotice(@PathVariable(value = "notificationId") Long noticeId) {
        noticeService.deleteNotification(noticeId);
        return ResponseHandler.handleSuccessResponse("Delete Success", HttpStatus.OK);
    }

    @GetMapping("/friends/{userId}")
    public ApiSuccessResponse<?> getFriendsList(
            @PathVariable("userId") Long userId
    ) {
        List<UserResponse> userResponses = noticeService.getFriendsList(userId);
        return ResponseHandler.handleSuccessResponse(userResponses, HttpStatus.OK);
    }


}
