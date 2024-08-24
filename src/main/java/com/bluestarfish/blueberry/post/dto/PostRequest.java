package com.bluestarfish.blueberry.post.dto;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private Long id;
    private Long userId;
    private Long roomId;
    private String title;
    private String content;
    private PostType postType;
    private Boolean isRecruited;

    public Post toEntity(User user, Room room) {
        return Post.builder()
                .user(user)
                .room(room)
                .title(title)
                .content(content)
                .postType(postType)
                .isRecruited(isRecruited)
                .build();
    }

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .postType(postType)
                .isRecruited(isRecruited)
                .build();
    }
}
