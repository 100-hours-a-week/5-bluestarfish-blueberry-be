package com.bluestarfish.blueberry.notification.service;

import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NoticeService {

    void sendNotice(Long userId, NoticeDto noticeDto);

    SseEmitter subscribe(Long userId);

    void updateNotificationStatus(Long userId, NoticeDto noticeDto);
}
