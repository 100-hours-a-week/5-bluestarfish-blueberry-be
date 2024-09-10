package com.bluestarfish.blueberry.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private MultipartFile profileImage;
    private String password;
}
