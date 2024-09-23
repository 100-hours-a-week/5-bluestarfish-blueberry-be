package com.bluestarfish.blueberry.post.entity;


import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.room.entity.Room;
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
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Room room;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PostType postType;

    @Column(name = "is_recruited")
    private boolean isRecruited;

    private boolean postCamEnabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Post(
            User user,
            Room room,
            String title,
            String content,
            PostType postType,
            boolean isRecruited,
            boolean postCamEnabled
    ) {
        this.user = user;
        this.room = room;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.isRecruited = isRecruited;
        this.postCamEnabled = postCamEnabled;
    }
}
