package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.*;

import java.io.IOException;

public interface UserService {
    UserResponse getUserByToken(String accessToken);

    void join(JoinRequest joinRequest);

    UserResponse findById(Long id);

    void update(Long id, UserUpdateRequest userUpdateRequest) throws IOException;

    void withdraw(Long id);

    void validateNickname(String nickname);

    void resetPassword(PasswordResetRequest passwordResetRequest);

    StudyTimeResponse getStudyTime(Long userId);

    void updateStudyTime(Long userId, StudyTimeUpdateRequest studyTimeUpdateRequest);
}
