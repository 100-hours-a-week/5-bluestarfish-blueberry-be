package com.bluestarfish.blueberry.webrtc.presentation.handler;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.google.gson.JsonObject;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public abstract class MessageHandler {
    protected WebRTCUserRegistry webRTCUserRegistry;
    protected WebRTCRoomManager webRTCRoomManager;

    public MessageHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        this.webRTCRoomManager = webRTCRoomManager;
        this.webRTCUserRegistry = webRTCUserRegistry;
    }

    public abstract void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException;

    protected UserSession findUserSession(WebSocketSession webSocketSession) {
        return webRTCUserRegistry.getBySession(webSocketSession);
    }
}
