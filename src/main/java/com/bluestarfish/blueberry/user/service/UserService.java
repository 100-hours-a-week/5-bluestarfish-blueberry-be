package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.*;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse getUserByToken(String accessToken);

    void join(JoinRequest joinRequest);

    UserResponse findById(Long id);

    void update(Long id, UserUpdateRequest userUpdateRequest, String accessToken) throws IOException;

    void withdraw(Long id, String accessToken);

    void validateNickname(String nickname);

    void resetPassword(PasswordResetRequest passwordResetRequest, String accessToken);

    StudyTimeResponse getStudyTime(Long userId);

    void updateStudyTime(Long userId, StudyTimeUpdateRequest studyTimeUpdateRequest);

    List<RankResponse> getRanks(Long userId);

    List<FoundUserResponse> searchUsers(String accessToken, String keyword);
}
