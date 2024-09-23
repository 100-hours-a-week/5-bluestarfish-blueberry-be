package com.bluestarfish.blueberry.notification.repository;

import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdAndDeletedAtIsNull(Long receiverId);

    List<Notification> findByReceiverIdAndNotiTypeAndNotiStatus(Long receiverId, NotiType notiType, NotiStatus notiStatus);

    List<Notification> findBySenderIdAndNotiTypeAndNotiStatus(Long senderId, NotiType notiType, NotiStatus notiStatus);
}
