package com.bluestarfish.blueberry.webrtc.presentation.handler.impl;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.presentation.handler.MessageHandler;
import com.google.gson.JsonObject;
import org.kurento.client.IceCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;
import static com.bluestarfish.blueberry.webrtc.util.JsonMessageParser.extractCandidate;

@Component(value = "onIceCandidate")
public class OnIceCandidateHandler extends MessageHandler {

    @Autowired
    public OnIceCandidateHandler(WebRTCRoomManager webRTCRoomManager, WebRTCUserRegistry webRTCUserRegistry) {
        super(webRTCRoomManager, webRTCUserRegistry);
    }

    @Override
    public void handleMessage(JsonObject jsonMessage, WebSocketSession webSocketSession) {
        UserSession userSession = findUserSession(webSocketSession);
        Optional.ofNullable(userSession)
                .ifPresent(
                        session -> {
                            JsonObject candidate = extractCandidate(jsonMessage);

                            userSession.addCandidate(
                                    new IceCandidate(
                                            candidate.get(CANDIDATE).getAsString(),
                                            candidate.get(SDP_MEDIA_ID).getAsString(), // 미디어 스트림을 고유하게 식별하는 ID.
                                            candidate.get(SDP_MEDIA_LINE_INDEX).getAsInt() // SDP에서 이 미디어 스트림이 몇 번째에 위치하는지를 나타내는 인덱스
                                    ),
                                    jsonMessage.get(NAME).getAsString()
                            );
                        }
                );

    }
}
