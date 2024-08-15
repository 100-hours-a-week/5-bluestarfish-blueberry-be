package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.entity.User;

public interface UserService {
    void join(JoinRequest joinRequest);
    UserResponse findById(Long userId);
}
