package com.bluestarfish.blueberry.auth.service;

import com.bluestarfish.blueberry.auth.dto.LoginRequest;
import com.bluestarfish.blueberry.auth.dto.LoginSuccessResult;
import com.bluestarfish.blueberry.auth.dto.MailAuthRequest;

public interface AuthService {
    LoginSuccessResult login(LoginRequest loginRequest);

    void logout(Long userId);

    void sendMail(MailAuthRequest mailAuthRequest);
}
