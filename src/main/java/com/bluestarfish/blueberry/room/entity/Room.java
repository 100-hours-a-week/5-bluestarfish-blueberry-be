package com.bluestarfish.blueberry.room.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String title;

    @Column(name = "max_users")
    private int maxUsers;

    @ColumnDefault("true")
    @Column(name = "cam_enabled")
    private boolean isCamEnabled;

    private String password;

    @Column(length = 4096)
    private String thumbnail;

    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Room(
            String title,
            int maxUsers,
            boolean isCamEnabled,
            String password,
            String thumbnail,
            String description
    ) {
        this.title = title;
        this.maxUsers = maxUsers;
        this.isCamEnabled = isCamEnabled;
        this.password = password;
        this.thumbnail = thumbnail;
        this.description = description;
    }
}
