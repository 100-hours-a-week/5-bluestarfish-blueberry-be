package com.bluestarfish.blueberry.webrtc.presentation.handler.impl;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.presentation.handler.MessageHandler;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;

@Slf4j
@Component(value = "pingPong")
public class PingPongHandler extends MessageHandler {

    @Autowired
    public PingPongHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        UserSession userSession = findUserSession(webSocketSession);

        if (jsonMessage.get(MESSAGE).getAsString().equals(PING_PONG_QUESTION)) {
            log.info("핑 도착");

            JsonObject pongMessage = new JsonObject();
            pongMessage.addProperty(SOCKET_MESSAGE_ID, PING_PONG);
            pongMessage.addProperty(MESSAGE, PING_PONG_ANSWER);
            userSession.sendMessage(pongMessage);

            return;
        }

        log.error("핑 메시지 에러: {}", jsonMessage);
    }
}
