package com.bluestarfish.blueberry.notification.service;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.comment.repository.CommentRepository;
import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.exception.NotificationException;
import com.bluestarfish.blueberry.notification.repository.NotificationRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final UserRepository userRepository;

    private final Map<Long, SseEmitter> clientEmitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final RoomRepository roomRepository;

    public NoticeServiceImpl(UserRepository userRepository, NotificationRepository notificationRepository,
                             CommentRepository commentRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.commentRepository = commentRepository;
        this.roomRepository = roomRepository;
    }

    //클라이언트가 알림을 구독(처음 진입시)
    @Override
    public SseEmitter subscribe(Long userId) {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clientEmitters.put(userId, emitter);

        emitter.onCompletion(() -> clientEmitters.remove(userId));
        emitter.onTimeout(() -> clientEmitters.remove(userId));

        return emitter;

    }

    public Notification sendNotice(Long userId, NoticeDto noticeDto) {
        SseEmitter emitter = clientEmitters.get(noticeDto.getReceiverId());
        if (emitter != null) {
            try {
                User sender = userRepository.findById(userId)
                        .orElseThrow(() -> new UserException("User id " + userId + " not found", HttpStatus.NOT_FOUND));
                User receiver = userRepository.findById(noticeDto.getReceiverId())
                        .orElseThrow(() -> new UserException(
                                "Receiver Id" + noticeDto.getReceiverId() + " not found", HttpStatus.NOT_FOUND));
                Comment comment =
                        noticeDto.getCommentId() != null ? commentRepository.findById(noticeDto.getCommentId())
                                .orElse(null) : null;
                Room room = noticeDto.getRoomId() != null ? roomRepository.findById(noticeDto.getRoomId()).orElse(null)
                        : null;

                Notification notification = Notification.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .notiType(noticeDto.getNotiType())
                        .notiStatus(noticeDto.getNotiStatus())
                        .comment(comment)
                        .room(room)
                        .build();

                Notification savedNotification = notificationRepository.save(notification);

                // SSE로 알림 전송 (저장된 알림 정보 전송)
                emitter.send(SseEmitter.event().name("notification").data(savedNotification));

                return savedNotification;

            } catch (IOException e) {
                clientEmitters.remove(noticeDto.getReceiverId());
            }
        }

        return null;
    }


    public Notification updateNotificationStatus(Long userId, Long noticeId, NoticeDto noticeDto) {
        SseEmitter emitter = clientEmitters.get(noticeDto.getReceiverId());

        if (emitter != null) {
            try {
                Notification notification = notificationRepository.findById(noticeId)
                        .orElseThrow(() -> new NotificationException("Notification not found", HttpStatus.NOT_FOUND));

                notification.setNotiStatus(noticeDto.getNotiStatus());
                Notification savedNotification = notificationRepository.save(notification);

                // SSE로 알림 전송 (저장된 알림 정보 전송)
                emitter.send(SseEmitter.event().name("notification").data(savedNotification.getNotiStatus()));

                return savedNotification;

            } catch (IOException e) {
                clientEmitters.remove(noticeDto.getReceiverId());
            }
        }
        return null;
    }

    @Override
    public List<NoticeDto> listNotifications(Long userId) {

        List<Notification> notifications = notificationRepository.findByReceiverIdAndDeletedAtIsNull(userId);

        return notifications.stream()
                .map(NoticeDto::from)
                .toList();
    }

    @Override
    public void deleteNotification(Long noticeId) {
        Notification notification = notificationRepository.findById(noticeId)
                .orElseThrow(() -> new NotificationException("Notification not found", HttpStatus.NOT_FOUND));
        notification.setDeletedAt(LocalDateTime.now());

        notificationRepository.save(notification);


    }


}
