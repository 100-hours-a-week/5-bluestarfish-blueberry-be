package com.bluestarfish.blueberry.notification.service;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.comment.repository.CommentRepository;
import com.bluestarfish.blueberry.exception.CustomException;
import com.bluestarfish.blueberry.exception.ExceptionDomain;
import com.bluestarfish.blueberry.notification.dto.NoticeDto;
import com.bluestarfish.blueberry.notification.entity.Notification;
import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import com.bluestarfish.blueberry.notification.repository.NotificationRepository;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Service
@Slf4j
@Transactional
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

        SseEmitter emitter = new SseEmitter(3600L * 1000L);
        //만료시간이 너무 길면 연결 관리를 해줘야하고 짧으면 재연결 요청이 잦아서 문제(적절함 필요)

        clientEmitters.put(userId, emitter);

        //더미 데이터 (sse 처음 연결후 더미데이터를 넘겨줘야함)
        NoticeDto noticeDto = NoticeDto.builder()
                .id(1L)
                .senderId(userId)
                .receiverId(userId)
                .notiType(NotiType.MENTION)
                .notiStatus(NotiStatus.CONNECTED)
                .commentId(null)
                .roomId(null)
                .build();

        sendMessage(userId, noticeDto);

        emitter.onCompletion(() -> {
            clientEmitters.remove(userId);
            log.info("연결 종료");
        });
        emitter.onTimeout(() -> {
            clientEmitters.remove(userId);
            log.info("타임아웃 종료");
        });

        return emitter;
    }

    public void sendMessage(Long userId, NoticeDto noticeDto) {
        SseEmitter emitter = clientEmitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(noticeDto.getNotiStatus()));
            } catch (IOException e) {
                clientEmitters.remove(userId);
                log.error("Error send notice " + e.getMessage(), e);
            }
        }
    }

    public Notification sendNotice(Long userId, NoticeDto noticeDto) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User id " + userId + " not found", ExceptionDomain.USER, HttpStatus.NOT_FOUND));
        User receiver = userRepository.findById(noticeDto.getReceiverId())
                .orElseThrow(() -> new CustomException(
                        "Receiver Id " + noticeDto.getReceiverId() + " not found",
                        ExceptionDomain.USER,
                        HttpStatus.NOT_FOUND
                ));

        Comment comment = (noticeDto.getCommentId() != null)
                ? commentRepository.findById(noticeDto.getCommentId())
                .orElseThrow(() -> new CustomException(
                        "Comment Id " + noticeDto.getCommentId() + " not found",
                        ExceptionDomain.COMMENT,
                        HttpStatus.NOT_FOUND
                )) : null;

        Room room = (noticeDto.getRoomId() != null)
                ? roomRepository.findById(noticeDto.getRoomId())
                .orElseThrow(() -> new CustomException(
                        "Room Id " + noticeDto.getRoomId() + " not found",
                        ExceptionDomain.ROOM,
                        HttpStatus.NOT_FOUND
                )) : null;

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .notiType(noticeDto.getNotiType())
                .notiStatus(noticeDto.getNotiStatus())
                .comment(comment)
                .room(room)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        SseEmitter emitter = clientEmitters.get(noticeDto.getReceiverId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(savedNotification));
            } catch (IOException e) {
                clientEmitters.remove(noticeDto.getReceiverId());
                log.error("Error send notice " + e.getMessage(), e);
            }
        } else {
            log.info("User is not online");
        }
        return savedNotification;
    }


    public Notification updateNotificationStatus(Long userId, Long noticeId, NoticeDto noticeDto) {
        SseEmitter emitter = clientEmitters.get(noticeDto.getReceiverId());

        if (emitter != null) {
            try {
                Notification notification = notificationRepository.findById(noticeId)
                        .orElseThrow(() -> new CustomException(
                                "Notification not found",
                                ExceptionDomain.NOTIFICATION,
                                HttpStatus.NOT_FOUND
                        ));

                notification.setNotiStatus(noticeDto.getNotiStatus());

                emitter.send(SseEmitter.event().name("notification").data(notification));

                return notification;

            } catch (IOException e) {
                clientEmitters.remove(noticeDto.getReceiverId());
            }
        }
        return null;
    }

    @Override
    public List<Notification> listNotifications(Long userId) {

        return notificationRepository.findByReceiverIdAndDeletedAtIsNull(userId);
    }

    @Override
    public List<UserResponse> getFriendsList(Long userId) {
        // 1. 친구 추가 요청을 받은사람(Receiver)가 본인(userId)일 때
        List<Notification> notifications = notificationRepository
                .findByReceiverIdAndNotiTypeAndNotiStatus(userId, NotiType.FRIEND, NotiStatus.ACCEPTED);

        // 그러면 Sender의 정보를 전달해야함
        List<UserResponse> userSenderResponses = notifications.stream()
                .map(notification ->
                        UserResponse.from(userRepository.findById(notification.getSender().getId())
                                .orElseThrow(() -> new NotificationException("No User has input userId", HttpStatus.NOT_FOUND)))
                )
                .toList();

        // 2. 친구 추가 요청을 받은사람이 상대(Receiver)일때, 즉 내(userId)가 친구 추가 요청을 보냈을때(Sender)
        notifications = notificationRepository.findBySenderIdAndNotiTypeAndNotiStatus(userId, NotiType.FRIEND, NotiStatus.ACCEPTED);

        // 그러면 Receiver의 정보 전달
        List<UserResponse> userReceiverResponses = notifications.stream()
                .map(notification ->
                        UserResponse.from(userRepository.findById(notification.getReceiver().getId())
                                .orElseThrow(() -> new NotificationException("No User has input userId", HttpStatus.NOT_FOUND)))
                )
                .toList();

        List<UserResponse> friends = new ArrayList<>(userSenderResponses);
        friends.addAll(userReceiverResponses);

        return friends;
    }

    @Override
    public void deleteNotification(Long noticeId) {
        Notification notification = notificationRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(
                        "Notification not found",
                        ExceptionDomain.NOTIFICATION,
                        HttpStatus.NOT_FOUND
                ));
        notification.setDeletedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}