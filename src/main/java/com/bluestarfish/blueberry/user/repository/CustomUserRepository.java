package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.dto.FoundUserResponse;

import java.util.List;

public interface CustomUserRepository {
    List<FoundUserResponse> findUsersByNickname(Long requestUserId, String keyword);
}
