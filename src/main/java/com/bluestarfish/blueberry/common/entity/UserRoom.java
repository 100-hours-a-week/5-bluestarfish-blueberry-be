package com.bluestarfish.blueberry.common.entity;

import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "users_rooms"
)
public class UserRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @Column(name = "is_host", nullable = false)
    private boolean isHost;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "cam_enabled", nullable = false)
    private boolean camEnabled;

    @Column(name = "mic_enabled", nullable = false)
    private boolean micEnabled;

    @Column(name = "speaker_enabled", nullable = false)
    private boolean speakerEnabled;

    @Column(name = "goal_time", nullable = false)
    private Time goalTime;

    @Column(name = "day_time", nullable = false)
    private Time dayTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public UserRoom(
            User user,
            Room room,
            boolean isHost,
            boolean isActive,
            boolean camEnabled,
            boolean micEnabled,
            boolean speakerEnabled,
            Time goalTime,
            Time dayTime
    ) {
        this.user = user;
        this.room = room;
        this.isHost = isHost;
        this.isActive = isActive;
        this.camEnabled = camEnabled;
        this.micEnabled = micEnabled;
        this.speakerEnabled = speakerEnabled;
        this.goalTime = goalTime;
        this.dayTime = dayTime;
    }


}
