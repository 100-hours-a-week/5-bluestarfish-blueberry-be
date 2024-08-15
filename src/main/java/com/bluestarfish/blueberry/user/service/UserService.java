package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.UpdateUserRequest;
import com.bluestarfish.blueberry.user.dto.UserResponse;

public interface UserService {
    void join(JoinRequest joinRequest);
    UserResponse findById(Long id);
    void update(Long id, UpdateUserRequest updateUserRequest);
    void withdraw(Long id);
}
