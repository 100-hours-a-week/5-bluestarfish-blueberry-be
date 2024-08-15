package com.bluestarfish.blueberry.notification.enumeration;

import lombok.Getter;

@Getter
public enum NotiType {
    MENTION("MENTION"),
    FRIEND("FRIEND"),
    ROOM("ROOM");

    private final String type;

    NotiType(String type) {
        this.type = type;
    }
}
