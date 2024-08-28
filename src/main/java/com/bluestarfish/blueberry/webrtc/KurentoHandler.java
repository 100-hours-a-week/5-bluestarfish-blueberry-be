package com.bluestarfish.blueberry.webrtc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KurentoHandler extends TextWebSocketHandler {
    private static final String JOIN_ROOM_MESSAGE = "joinRoom";
    private static final String RECEIVE_VIDEO_FROM_MESSAGE = "receiveVideoFrom";
    private static final String LEAVE_ROOM_MESSAGE = "leaveRoom";
    private static final String ON_ICE_CANDIDATE_MESSAGE = "onIceCandidate";

    private static final Gson gson = new GsonBuilder().create();
    private final WebRTCRoomManager webRTCRoomManager;
    private final WebRTCUserRegistry registry;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        UserSession user = registry.getBySession(session);

        if (user != null) {
            log.debug("Incoming message from user '{}': {}", user.getName(), jsonMessage);
        } else {
            log.debug("Incoming message from new user: {}", jsonMessage);
        }

        switch (jsonMessage.get("id").getAsString()) {
            case JOIN_ROOM_MESSAGE:
                joinRoom(jsonMessage, session);
                break;
            case RECEIVE_VIDEO_FROM_MESSAGE:
                final String senderName = jsonMessage.get("sender").getAsString();
                final UserSession sender = registry.getByName(senderName);
                final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
                user.receiveVideoFrom(sender, sdpOffer);
                break;
            case LEAVE_ROOM_MESSAGE:
                leaveRoom(user);
                break;
            case ON_ICE_CANDIDATE_MESSAGE:
                JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

                if (user != null) {
                    IceCandidate cand = new IceCandidate(candidate.get("candidate").getAsString(),
                            candidate.get("sdpMid").getAsString(), candidate.get("sdpMLineIndex").getAsInt());
                    user.addCandidate(cand, jsonMessage.get("name").getAsString());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserSession user = registry.removeBySession(session);
        webRTCRoomManager.getRoom(user.getRoomName()).leave(user);
    }

    private void joinRoom(JsonObject params, WebSocketSession session) throws IOException {
        final String roomName = params.get("room").getAsString();
        final String name = params.get("name").getAsString();
        log.info("PARTICIPANT {}: trying to join room {}", name, roomName);

        WebRTCRoom room = webRTCRoomManager.getRoom(roomName);
        final UserSession user = room.join(name, session);
        registry.register(user);
    }

    private void leaveRoom(UserSession user) throws IOException {
        WebRTCRoom room = webRTCRoomManager.getRoom(user.getRoomName());
        room.leave(user);
        if (room.getParticipants().isEmpty()) {
            webRTCRoomManager.removeRoom(room);
        }
    }
}
