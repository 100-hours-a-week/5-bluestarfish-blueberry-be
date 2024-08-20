package com.bluestarfish.blueberry.oauth2;

import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.enumeration.AuthType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OAuth2UserDTO {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private AuthType authType;
    private LocalDateTime createdAt;

    public static OAuth2UserDTO from(User user) {
        return OAuth2UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .authType(user.getAuthType())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .authType(authType)
                .build();
    }
}
