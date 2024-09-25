package com.bluestarfish.blueberry.webrtc.presentation.handler.impl;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.presentation.handler.MessageHandler;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.IS_MIC_ON;

@Component(value = "isMicOn")
public class IsMicOnHandler extends MessageHandler {

    @Autowired
    public IsMicOnHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) {
        UserSession userSession = findUserSession(webSocketSession);
        userSession.updateMicEnabled(jsonMessage.get(IS_MIC_ON).getAsBoolean());
        webRTCRoomManager.sendMicControl(jsonMessage, userSession);
    }
}
