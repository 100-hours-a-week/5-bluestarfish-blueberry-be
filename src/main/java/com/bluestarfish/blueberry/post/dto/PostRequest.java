package com.bluestarfish.blueberry.post.dto;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title must not be blank")
    @Size(min = 5, max = 20, message = "Title must be between 5 and 20 characters")
    private String title;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 5, max = 200, message = "Content must be between 5 and 200 characters")
    private String content;

    private PostType type;
    private Boolean isRecruited;
    private boolean postCamEnabled;

    public Post toEntity(User user, Room room) {
        return Post.builder()
                .user(user)
                .room(room)
                .title(title)
                .content(content)
                .postType(type)
                .isRecruited(isRecruited)
                .postCamEnabled(postCamEnabled)
                .build();
    }

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .postType(type)
                .isRecruited(isRecruited)
                .postCamEnabled(postCamEnabled)
                .build();
    }
}
