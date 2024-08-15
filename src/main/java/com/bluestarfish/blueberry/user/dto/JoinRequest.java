package com.bluestarfish.blueberry.user.dto;

import com.bluestarfish.blueberry.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class JoinRequest {
    private String email;
    private String nickname;
    @Setter
    private String password;

    public User toEntity() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
