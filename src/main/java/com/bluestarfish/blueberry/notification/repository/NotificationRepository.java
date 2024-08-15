package com.bluestarfish.blueberry.notification.repository;

import com.bluestarfish.blueberry.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
