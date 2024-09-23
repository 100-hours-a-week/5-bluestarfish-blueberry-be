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

@Component(value = "afterConnectionClosed")
public class AfterConnectionClosedHandler extends MessageHandler {

    @Autowired
    public AfterConnectionClosedHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        UserSession userSession = webRTCUserRegistry.removeBySession(webSocketSession);
        webRTCRoomManager.getRoom(userSession.getRoomName()).leave(userSession);
    }
}
