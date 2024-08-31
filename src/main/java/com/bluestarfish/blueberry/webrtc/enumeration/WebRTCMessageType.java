package com.bluestarfish.blueberry.webrtc.enumeration;

public enum WebRTCMessageType {
    JOIN_ROOM("joinRoom"),
    RECEIVE_VIDEO_FROM("receiveVideoFrom"),
    LEAVE_ROOM("leaveRoom"),
    ON_ICE_CANDIDATE("onIceCandidate");

    private final String type;

    WebRTCMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}


