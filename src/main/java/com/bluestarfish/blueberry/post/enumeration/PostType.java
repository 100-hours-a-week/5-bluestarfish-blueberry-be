package com.bluestarfish.blueberry.post.enumeration;

import lombok.Getter;

@Getter
public enum PostType {
    FINDING_MEMBERS("FINDING_MEMBERS"),
    FINDING_ROOMS("FINDING_ROOMS");

    private final String type;

    PostType(String type) {
        this.type = type;
    }
}
