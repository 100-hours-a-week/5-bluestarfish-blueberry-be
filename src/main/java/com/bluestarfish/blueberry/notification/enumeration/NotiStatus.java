package com.bluestarfish.blueberry.notification.enumeration;

import lombok.Getter;

@Getter
public enum NotiStatus {
    CONNECTED("CONNECTED"),
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED");

    private final String type;

    NotiStatus(String type) {
        this.type = type;
    }
}
