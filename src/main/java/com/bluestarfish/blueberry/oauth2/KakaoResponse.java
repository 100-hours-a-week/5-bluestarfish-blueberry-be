package com.bluestarfish.blueberry.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoResponse {
    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.attribute.put("id", attribute.get("id"));
    }

    public String getEmail() {
        return attribute.get("email").toString() + "@" + attribute.get("id").toString();
    }
}
