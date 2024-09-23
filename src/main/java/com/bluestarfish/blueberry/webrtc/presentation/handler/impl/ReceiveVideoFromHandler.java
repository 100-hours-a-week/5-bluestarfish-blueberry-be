package com.bluestarfish.blueberry.webrtc.presentation.handler.impl;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.presentation.handler.MessageHandler;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.SENDER;

@Component(value = "receiveVideoFrom")
public class ReceiveVideoFromHandler extends MessageHandler {

    @Autowired
    public ReceiveVideoFromHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        UserSession userSession = findUserSession(webSocketSession);
        webRTCRoomManager.receiveVideoFrom(jsonMessage, userSession, findUserSession(jsonMessage));
    }

    private UserSession findUserSession(JsonObject jsonMessage) {
        return webRTCUserRegistry.getByName(jsonMessage.get(SENDER).getAsString());
    }
}
