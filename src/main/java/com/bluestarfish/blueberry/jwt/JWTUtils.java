package com.bluestarfish.blueberry.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTUtils {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID = "id";
    private static final String TOKEN_KEY = "typ";
    private static final String TOKEN_VALUE = "JWT";

    @Value("${jwt.accessExpired}")
    private Long expiredTime;

    @Value("${jwt.refreshExpired}")
    private Long refreshExpiredTime;

    @Value("${jwt.secretKey}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public Long getId(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(USER_ID, Long.class);
    }

    public boolean isExpired(String token) {
        System.out.println(token);
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(7);
        }

        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public JWTTokens createJwt(Long userId) {
        String accessToken = TOKEN_PREFIX + Jwts.builder()
                .header()
                .add(TOKEN_KEY, TOKEN_VALUE)
                .and()
                .claim(USER_ID, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(secretKey)
                .compact();

        String refreshToken = TOKEN_PREFIX + Jwts.builder()
                .header()
                .add(TOKEN_KEY, TOKEN_VALUE)
                .and()
                .claim(USER_ID, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiredTime))
                .signWith(secretKey)
                .compact();

        return new JWTTokens(accessToken, refreshToken);
    }
}
