package com.bluestarfish.blueberry.user.dto;

import com.bluestarfish.blueberry.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequest {
    private String email;
    private String nickname;
    private String password;

    public User toEntity() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
