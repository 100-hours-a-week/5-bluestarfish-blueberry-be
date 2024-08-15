package com.bluestarfish.blueberry.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String nickname;
    private String profileImage;
    private String password;
}
