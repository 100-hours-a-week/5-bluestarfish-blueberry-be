package com.bluestarfish.blueberry.auth.entity;

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
        name = "auth_codes",
        indexes = {
                @Index(name = "idx_email", columnList = "email")
        }
)
public class AuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String email;

    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public AuthCode(
            String email,
            String code
    ) {
        this.email = email;
        this.code = code;
    }
}
