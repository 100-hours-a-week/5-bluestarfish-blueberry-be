package com.bluestarfish.blueberry.common.dto;

import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRoomResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean isHost;
    private boolean camEnabled;
    private boolean micEnabled;
    private boolean speakerEnabled;

    public static UserRoomResponse from(UserRoom userRoom, User user) {
        return UserRoomResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .isHost(userRoom.isHost())
                .camEnabled(userRoom.isCamEnabled())
                .micEnabled(userRoom.isMicEnabled())
                .speakerEnabled(userRoom.isSpeakerEnabled())
                .build();
    }
}
