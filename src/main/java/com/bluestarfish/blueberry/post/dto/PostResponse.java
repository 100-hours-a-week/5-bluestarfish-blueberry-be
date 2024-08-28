package com.bluestarfish.blueberry.post.dto;

import com.bluestarfish.blueberry.post.entity.Post;
import com.bluestarfish.blueberry.post.enumeration.PostType;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private PostType type;
    private boolean isRecruited;
    private boolean postCamEnabled;
    private UserResponse userResponse;
    private RoomResponse roomResponse;
//    private Long userId;
//    private String userEmail;
//    private String userNickName;
//    private String userProfileImage;
//    private Room room;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static PostResponse from(Post post, int roomMember) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getPostType())
                .isRecruited(post.isRecruited())
                .postCamEnabled(post.isPostCamEnabled())
                .userResponse(UserResponse.from(post.getUser()))
                .roomResponse(RoomResponse.from(post.getRoom(), roomMember))
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getPostType())
                .isRecruited(post.isRecruited())
                .postCamEnabled(post.isPostCamEnabled())
                .userResponse(UserResponse.from(post.getUser()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
