package com.bluestarfish.blueberry.util;

import jakarta.servlet.http.Cookie;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieCreator {
    private static final String AUTH_KEY = "Authorization";
    private static final String AUTH_PATH = "/";
    private static final Integer MAX_AGE = 60 * 60 * 24;

    public static Cookie createAuthCookie(String accessToken) {
        Cookie accessTokenCookie = new Cookie(AUTH_KEY, URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath(AUTH_PATH);
        accessTokenCookie.setMaxAge(MAX_AGE);

        return accessTokenCookie;
    }

    public static Cookie removeAuthCookie() {
        Cookie cookie = new Cookie(AUTH_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath(AUTH_PATH);

        return cookie;
    }
}
