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

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.IS_CAM_ON;

@Component(value = "isCamOn")
public class IsCamOnHandler extends MessageHandler {

    @Autowired
    public IsCamOnHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        UserSession userSession = findUserSession(webSocketSession);
        userSession.updateCamEnabled(jsonMessage.get(IS_CAM_ON).getAsBoolean());
        webRTCRoomManager.sendCamControl(jsonMessage, userSession);
    }
}
