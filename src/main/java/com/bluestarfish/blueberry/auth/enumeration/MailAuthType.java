package com.bluestarfish.blueberry.auth.enumeration;

import lombok.Getter;

@Getter
public enum MailAuthType {
    JOIN("join"),
    RESET("reset");

    private final String type;

    MailAuthType(String type) {
        this.type = type;
    }
}
