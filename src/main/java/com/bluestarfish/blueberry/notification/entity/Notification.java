package com.bluestarfish.blueberry.notification.entity;

import com.bluestarfish.blueberry.comment.entity.Comment;
import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private NotiStatus notiStatus;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Notification(
            User sender,
            User receiver,
            NotiType notiType,
            NotiStatus notiStatus,
            Comment comment,
            Room room
    ) {
        this.sender = sender;
        this.receiver = receiver;
        this.notiType = notiType;
        this.notiStatus = notiStatus;
        this.comment = comment;
        this.room = room;
    }
}
