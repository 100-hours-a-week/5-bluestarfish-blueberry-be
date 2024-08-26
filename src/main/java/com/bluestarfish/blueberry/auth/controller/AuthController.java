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

    // FIXME: 인증 쿠키 만료, 리프레쉬 토큰만료

    @PostMapping("/logout")
    public ApiSuccessResponse<?> logout(
            @CookieValue("Authorization") String accessToken,
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie("Authorization", null); // 쿠키의 값을 null로 설정
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        
        authService.logout(accessToken);
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
