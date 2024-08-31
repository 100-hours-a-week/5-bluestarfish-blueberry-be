package com.bluestarfish.blueberry.webrtc.handler;

import com.bluestarfish.blueberry.webrtc.UserSession;
import com.bluestarfish.blueberry.webrtc.WebRTCRoom;
import com.bluestarfish.blueberry.webrtc.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.WebRTCUserRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class KurentoHandler extends TextWebSocketHandler {

    private final Gson gson;
    private final WebRTCUserRegistry webRTCUserRegistry;
    private final WebRTCRoomManager webRTCRoomManager;

    @Override
    public void handleTextMessage(
            @Nullable final WebSocketSession webSocketSession,
            @Nullable final TextMessage textMessage
    ) throws Exception {
        JsonObject jsonMessage = convertToJsonObject(textMessage);
        UserSession userSession = findUserSession(webSocketSession);

        printUserMessage(userSession, jsonMessage);

        switch (jsonMessage.get(SOCKET_MESSAGE_ID).getAsString()) {
            case JOIN_ROOM:
                joinRoom(jsonMessage, webSocketSession);
                break;
            case RECEIVE_VIDEO_FROM:
                receiveVideoFrom(userSession, jsonMessage);
                break;
            case LEAVE_ROOM:
                leaveRoom(userSession);
                break;
            case ON_ICE_CANDIDATE:
                onIceCandidate(jsonMessage, userSession);
                break;
            default:
                break;
        }
    }

    private void onIceCandidate(
            JsonObject jsonMessage,
            UserSession userSession
    ) {
        Optional.ofNullable(userSession)
                .ifPresent(session -> {
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

    private JsonObject extractCandidate(JsonObject jsonMessage) {
        return jsonMessage.get(CANDIDATE).getAsJsonObject();
    }

    private void receiveVideoFrom(
            UserSession userSession, JsonObject jsonMessage
    ) throws IOException {
        userSession.receiveVideoFrom(
                findUserSession(jsonMessage),
                extractSdpOffer(jsonMessage)
        );
    }

    private String extractSdpOffer(JsonObject jsonMessage) {
        return jsonMessage.get(SDP_OFFER).getAsString();
    }

    private UserSession findUserSession(JsonObject jsonMessage) {
        return webRTCUserRegistry.getByName(jsonMessage.get(SENDER).getAsString());
    }

    private void printUserMessage(UserSession userSession, JsonObject jsonMessage) {
        Optional.ofNullable(userSession)
                .ifPresentOrElse(
                        session -> log.info("'{}' 님의 메시지 도착: {}", session.getName(), jsonMessage),
                        () -> log.info("새로운 유저의 메시지 도착: {}", jsonMessage)
                );
    }

    private UserSession findUserSession(WebSocketSession webSocketSession) {
        return webRTCUserRegistry.getBySession(webSocketSession);
    }

    private JsonObject convertToJsonObject(TextMessage textMessage) {
        return gson.fromJson(textMessage.getPayload(), JsonObject.class);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserSession user = webRTCUserRegistry.removeBySession(session);
        webRTCRoomManager.getRoom(user.getRoomName()).leave(user);
    }

    private void joinRoom(JsonObject params, WebSocketSession session) throws IOException {
        final String roomName = params.get(ROOM).getAsString();
        final String name = params.get(NAME).getAsString();
        log.info("'{}' 님이 '{}' 방에 참여를 시도 중", name, roomName);

        WebRTCRoom room = webRTCRoomManager.getRoom(roomName);
        final UserSession user = room.join(name, session);
        webRTCUserRegistry.register(user);
    }

    private void leaveRoom(UserSession user) throws IOException {
        WebRTCRoom room = webRTCRoomManager.getRoom(user.getRoomName());
        room.leave(user);
        if (room.getParticipants().isEmpty()) {
            webRTCRoomManager.removeRoom(room);
        }
    }
}
