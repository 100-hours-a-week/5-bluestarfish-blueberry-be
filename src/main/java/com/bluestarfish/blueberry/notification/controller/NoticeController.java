package com.bluestarfish.blueberry.notification.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.handler.ResponseHandler;
import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        // 알림 서비스에서 구독 처리
        return noticeService.subscribe(userId);
    }

    // 알림 전송 처리
    @PostMapping("/{userId}/notifications")
    public ApiSuccessResponse<?> sendInviteNotification(@PathVariable(value = "userId") Long userId,
                                                        @RequestBody NoticeDto noticeDto) {

        // 알림을 저장하고 대상 사용자에게 전송
        noticeService.sendNotice(userId, noticeDto);

        return ResponseHandler.handleSuccessResponse("Send Notification Success", HttpStatus.OK);
    }

    // 알림 수락 처리: 알림을 수락했을 때 호출되는 엔드포인트
    @PatchMapping("/{userId}/notifications")
    public ApiSuccessResponse<?> acceptNotice(@PathVariable(value = "userId") Long userId,
                                              @RequestBody NoticeDto noticeDto) {
        noticeService.updateNotificationStatus(userId, noticeDto);
        return ResponseHandler.handleSuccessResponse("Request Accepted", HttpStatus.OK);
    }

    // 알림 거절 처리: 알림을 거절했을 때 호출되는 엔드포인트
    @PatchMapping("/{userId}/reject")
    public ApiSuccessResponse<?> rejectNotice(@PathVariable(value = "userId") Long userId,
                                              @RequestBody NoticeDto noticeDto) {
        noticeService.updateNotificationStatus(userId, noticeDto);
        return ResponseHandler.handleSuccessResponse("Request Declined", HttpStatus.OK);
    }

}
