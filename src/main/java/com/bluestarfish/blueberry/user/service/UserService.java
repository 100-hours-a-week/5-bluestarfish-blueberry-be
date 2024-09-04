package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.PasswordResetRequest;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.dto.UserUpdateRequest;

import java.io.IOException;

public interface UserService {
    UserResponse getUserByToken(String accessToken);

    void join(JoinRequest joinRequest);

    UserResponse findById(Long id);

    void update(Long id, UserUpdateRequest userUpdateRequest) throws IOException;

    void withdraw(Long id);

    void validateNickname(String nickname);

    void resetPassword(PasswordResetRequest passwordResetRequest);

}
