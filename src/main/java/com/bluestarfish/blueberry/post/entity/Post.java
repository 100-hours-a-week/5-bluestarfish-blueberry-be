package com.bluestarfish.blueberry.post.entity;


import com.bluestarfish.blueberry.post.enumeration.PostType;
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
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private PostType postType;

    @Column(name="is_recruited")
    private boolean isRecruited;

    private boolean postCamEnabled;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
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
