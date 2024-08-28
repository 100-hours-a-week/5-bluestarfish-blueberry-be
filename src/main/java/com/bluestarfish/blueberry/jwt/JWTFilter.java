package com.bluestarfish.blueberry.jwt;

import com.bluestarfish.blueberry.auth.entity.RefreshToken;
import com.bluestarfish.blueberry.auth.exception.AuthException;
import com.bluestarfish.blueberry.auth.repository.RefreshTokenRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_KEY = "Authorization";
    private static final String USER_ID_KEY = "user-id";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PATCH = "PATCH";
    private static final String HTTP_METHOD_DELETE = "DELETE";
    private static final String HEALTH_CHECK_URL = "/api/v1/health";
    private static final String FEEDBACK_URL = "/api/v1/feedback";
    private static final String LOGIN_URL = "/api/v1/auth/login";
    private static final String LOGOUT_URL = "/api/v1/auth/logout";
    private static final String MAIL_AUTH_URL = "/api/v1/auth/mail";
    private static final String JOIN_URL = "/api/v1/users";
    private static final String WHOAMI_URL = "/api/v1/users/whoami";
    private static final String VALIDATE_NICKNAME_URL = "/api/v1/users/nickname";
    private static final String RESET_PASSWORD_URL = "/api/v1/users/password";
    private static final String FIND_ROOMS_URL = "/api/v1/rooms";
    private static final String FIND_POSTS_URL = "/api/v1/posts";
    private static final String OAUTH_REDIRECT_URL = "/login/oauth2/code/kakao";
    private static final String OAUTH_PAGE_URL = "/oauth2/authorization/kakao";


    private static final Map<String, List<String>> excludedUrls;

    static {
        Map<String, List<String>> tempMap = new HashMap<>();
        tempMap.put(HEALTH_CHECK_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(FEEDBACK_URL, List.of(HTTP_METHOD_GET, HTTP_METHOD_POST));
        tempMap.put(LOGIN_URL, List.of(HTTP_METHOD_POST));
        tempMap.put(LOGOUT_URL, List.of(HTTP_METHOD_POST));
        tempMap.put(MAIL_AUTH_URL, List.of(HTTP_METHOD_GET, HTTP_METHOD_POST));
        tempMap.put(JOIN_URL, List.of(HTTP_METHOD_POST));
        tempMap.put(VALIDATE_NICKNAME_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(RESET_PASSWORD_URL, List.of(HTTP_METHOD_PATCH));
        tempMap.put(FIND_ROOMS_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(FIND_POSTS_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(OAUTH_REDIRECT_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(OAUTH_PAGE_URL, List.of(HTTP_METHOD_GET));
        tempMap.put(WHOAMI_URL, List.of(HTTP_METHOD_GET));

        excludedUrls = tempMap;
    }


    private final JWTUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // FIXME: 임시 필터링 제거 이후 삭제
        if (true) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        if (excludedUrls.containsKey(requestUri) && excludedUrls.get(requestUri).contains(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println(requestUri);
        if (requestUri.contains("oauth2")) {
            System.out.println(requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthException("The cookie containing the user ID is absent", HttpStatus.UNAUTHORIZED);
        }

        String authorization = Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_KEY.equals(cookie.getName()))
                .findFirst()
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .orElse(null);

        Long userId = jwtUtils.getId(authorization);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("The user ID contained within the cookie is absent",
                        HttpStatus.NOT_FOUND));

        if (!isToken(authorization)) {
            throw new AuthException("Invalid Token", HttpStatus.UNAUTHORIZED);
        }

        try {
            if (isExpired(authorization)) {
                String refreshToken = findRefreshToken(userId).getToken();

                if (isExpired(refreshToken)) {
                    discardRefreshToken(userId);
                    throw new AuthException("Refresh Token is expired", HttpStatus.UNAUTHORIZED);
                }

                // 리프레쉬 토큰이 살아있다면
                JWTTokens jwtTokens = reissueJwt(userId);
                discardRefreshToken(userId);
                saveRefreshToken(user, jwtTokens.refreshToken());
                response.addCookie(createCookie(ACCESS_TOKEN_KEY,
                        URLEncoder.encode(jwtTokens.accessToken(), StandardCharsets.UTF_8)));
            }

            filterChain.doFilter(request, response);

        } catch (EmptyResultDataAccessException e) {
            throw new AuthException("Invalid access to token", HttpStatus.UNAUTHORIZED);
        }
    }


    private JWTTokens reissueJwt(Long userId) {
        return jwtUtils.createJwt(userId);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .build()
        );
    }

    private void discardRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private RefreshToken findRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException("No refresh token found for the user with " + userId,
                        HttpStatus.UNAUTHORIZED));
    }

    private boolean isExpired(String token) {
        return jwtUtils.isExpired(token);
    }

    private boolean isToken(String authorization) {
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(3600000);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
