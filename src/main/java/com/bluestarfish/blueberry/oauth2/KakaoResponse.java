package com.bluestarfish.blueberry.oauth2;

import java.util.Map;

public class KakaoResponse {
    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.attribute.put("id", attribute.get("id"));

    }
    
    public String getProviderId() {
        return attribute.get("id").toString();
    }


    public String getEmail() {
        return attribute.get("email").toString();
    }

    public String getProfileImage() {
        return ((Map<String, Object>) attribute.get("profile")).get("profile_image_url").toString();
    }
}
