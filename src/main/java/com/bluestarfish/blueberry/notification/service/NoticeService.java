package com.bluestarfish.blueberry.notification.service;

import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NoticeService {

    Notification sendNotice(Long userId, NoticeDto noticeDto);

    SseEmitter subscribe(Long userId);

    Notification updateNotificationStatus(Long userId, Long noticeId, NoticeDto noticeDto);

    void deleteNotification(Long noticeId);
  
    List<Notification> listNotifications(Long userId);

    List<UserResponse> getFriendsList(Long userId);
}
