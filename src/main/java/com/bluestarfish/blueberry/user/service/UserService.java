package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.JoinRequest;

public interface UserService {
    void join(JoinRequest joinRequest);
}
