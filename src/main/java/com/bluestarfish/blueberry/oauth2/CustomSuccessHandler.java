package com.bluestarfish.blueberry.oauth2;

import com.bluestarfish.blueberry.auth.repository.RefreshTokenRepository;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${frontend.server.ip}")
    private String frontEndServerIp;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        response.addCookie(createCookie("Authorization", URLEncoder.encode(customOAuth2User.getToken(), StandardCharsets.UTF_8), true));
        response.addCookie(createCookie("user-id", String.valueOf(customOAuth2User.getUserId()), false));
        response.sendRedirect(frontEndServerIp + "/");
    }

    private Cookie createCookie(String key, String value, Boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(3600000);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);

        return cookie;
    }
}
