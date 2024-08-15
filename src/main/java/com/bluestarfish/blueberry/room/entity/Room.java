package com.bluestarfish.blueberry.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String title;

    @Column(name="max_users")
    private int maxUsers;

    @ColumnDefault("true")
    @Column(name="cam_enabled", nullable = false)
    private boolean camEnabled;

    private String password;

    private String thumbnail;

    private String description;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Room(
        String title,
        int maxUsers,
        boolean camEnabled,
        String password,
        String thumbnail,
        String description
    ) {
        this.title = title;
        this.maxUsers = maxUsers;
        this.camEnabled = camEnabled;
        this.password = password;
        this.thumbnail = thumbnail;
        this.description = description;
    }
}
