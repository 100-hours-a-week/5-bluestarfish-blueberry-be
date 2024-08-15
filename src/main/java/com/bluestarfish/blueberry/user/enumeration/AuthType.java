package com.bluestarfish.blueberry.user.enumeration;

import lombok.Getter;

@Getter
public enum AuthType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO");

    private final String type;

    AuthType(String type) {
        this.type = type;
    }
}
