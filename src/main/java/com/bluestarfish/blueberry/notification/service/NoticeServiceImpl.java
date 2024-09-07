package com.bluestarfish.blueberry.notification.service;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.comment.repository.CommentRepository;
import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.repository.NotificationRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    //특정 사용자에게 알림 전송
    public void sendNotice(Long userId, NoticeDto noticeDto) {
        SseEmitter emitter = clientEmitters.get(noticeDto.getReceiverId());
        if (emitter != null) {
            try {
                User sender = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
                User receiver = userRepository.findById(noticeDto.getReceiverId())
                        .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
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

                // 알림 저장
                notificationRepository.save(notification);

                // SSE로 알림 전송
                emitter.send(SseEmitter.event().name("notification").data("Send Notification Success"));

            } catch (IOException e) {
                clientEmitters.remove(noticeDto.getReceiverId());
            }
        }
    }

    @Override
    public void updateNotificationStatus(Long userId, NoticeDto noticeDto) {

    }
}
