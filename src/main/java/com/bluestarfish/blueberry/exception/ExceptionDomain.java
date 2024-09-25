package com.bluestarfish.blueberry.exception;

import lombok.Getter;

@Getter
public enum ExceptionDomain {
    USER("USER"),
    AUTH("AUTH"),
    ROOM("ROOM"),
    POST("POST"),
    COMMENT("COMMENT"),
    FEEDBACK("FEEDBACK"),
    USERROOM("USERROOM"),
    NOTIFICATION("NOTIFICATION"),
    ROOMCHAT("ROOMCHAT");

    private final String domain;

    ExceptionDomain(String domain) {
        this.domain = domain;
    }
}
