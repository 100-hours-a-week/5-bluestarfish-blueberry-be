package com.bluestarfish.blueberry.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private String profileImage;
    private String password;
}
