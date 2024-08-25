package com.bluestarfish.blueberry.auth.controller;

import com.bluestarfish.blueberry.auth.dto.LoginRequest;
import com.bluestarfish.blueberry.auth.dto.LoginSuccessResult;
import com.bluestarfish.blueberry.auth.dto.MailAuthRequest;
import com.bluestarfish.blueberry.auth.dto.MailRequest;
import com.bluestarfish.blueberry.auth.service.AuthService;
import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // FIXME: 어세스토큰만 쿠키에 담고 userId 요청하는 API 추가

    @PostMapping("/login")
    public ApiSuccessResponse<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) throws UnsupportedEncodingException {

        LoginSuccessResult loginSuccessResult = authService.login(loginRequest);

        Cookie accessTokenCookie = new Cookie("Authorization", URLEncoder.encode(loginSuccessResult.getAccessToken(), "UTF-8"));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(accessTokenCookie);

        return handleSuccessResponse(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<?> logout(
            @CookieValue("user-id") Long userId
    ) {
        authService.logout(userId);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/mail")
    public ApiSuccessResponse<?> sendMail(
            @RequestBody MailRequest mailRequest
    ) {
        authService.sendMail(mailRequest);
        return handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping("/mail")
    public ApiSuccessResponse<?> authenticateCode(
            MailAuthRequest mailAuthRequest
    ) {
        System.out.println("mailAuthRequest = " + mailAuthRequest.getEmail());
        System.out.println("mailAuthRequest = " + mailAuthRequest.getCode());
        authService.authenticateCode(mailAuthRequest);
        return handleSuccessResponse(HttpStatus.OK);
    }
}
