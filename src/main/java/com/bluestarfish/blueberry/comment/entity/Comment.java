package com.bluestarfish.blueberry.comment.entity;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_post_id", columnList = "post_id")
        }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mention_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User metionedUser;

    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Comment(
            Post post,
            User user,
            User mentionedUser,
            String content
    ) {
        this.post = post;
        this.user = user;
        this.metionedUser = mentionedUser;
        this.content = content;
    }
}
