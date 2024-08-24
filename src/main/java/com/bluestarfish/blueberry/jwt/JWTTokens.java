package com.bluestarfish.blueberry.jwt;

public record JWTTokens(
        String accessToken,
        String refreshToken
) {
}
