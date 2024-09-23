package com.bluestarfish.blueberry.user.entity;


import com.bluestarfish.blueberry.user.enumeration.AuthType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_nickname", columnList = "nickname")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(name = "profile_image", length = 4096)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public User(
            Long id,
            String email,
            String password,
            String nickname,
            String profileImage,
            AuthType authType,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authType = authType;
        this.createdAt = createdAt;
    }
}
