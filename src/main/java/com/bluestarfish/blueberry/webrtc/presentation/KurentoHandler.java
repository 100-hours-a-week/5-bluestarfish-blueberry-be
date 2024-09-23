package com.bluestarfish.blueberry.webrtc.presentation;

import com.bluestarfish.blueberry.webrtc.application.WebRTCRoomManager;
import com.bluestarfish.blueberry.webrtc.application.WebRTCUserRegistry;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.domain.WebRTCRoom;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.commons.exception.KurentoException;
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
        Optional.ofNullable(textMessage).orElseThrow(
                () -> new KurentoException("")
        );

        JsonObject jsonMessage = convertToJsonObject(textMessage);
        UserSession userSession = findUserSession(webSocketSession);
        printUserMessage(userSession, jsonMessage);

        switch (extractMessageId(jsonMessage)) {
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
            case IS_CAM_ON:
                isCamOn(jsonMessage, userSession);
                break;
            case IS_MIC_ON:
                isMicOn(jsonMessage, userSession);
                break;
            case PING_PONG:
                pingPong(jsonMessage, userSession);
            default:
                break;
        }
    }

    private void pingPong(
            JsonObject jsonMessage,
            UserSession userSession
    ) throws IOException {
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

    private void isCamOn(
            JsonObject jsonMessage,
            UserSession userSession
    ) {
        WebRTCRoom webRTCRoom = webRTCRoomManager.getRoom(
                userSession.getRoomName()
        );
        webRTCRoom.sendCamControl(jsonMessage, userSession);
    }

    private void isMicOn(
            JsonObject jsonMessage,
            UserSession userSession
    ) {
        WebRTCRoom webRTCRoom = webRTCRoomManager.getRoom(
                userSession.getRoomName()
        );
        webRTCRoom.sendMicControl(jsonMessage, userSession);
    }

    private String extractMessageId(JsonObject jsonMessage) {
        return jsonMessage.get(SOCKET_MESSAGE_ID).getAsString();
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
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        UserSession userSession = webRTCUserRegistry.removeBySession(webSocketSession);
        webRTCRoomManager.getRoom(userSession.getRoomName()).leave(userSession);
    }

    private void joinRoom(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        webRTCUserRegistry.register(tryToJoinRoom(jsonMessage, webSocketSession));
    }

    private UserSession tryToJoinRoom(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        return webRTCRoomManager
                .getRoom(extractRoomId(jsonMessage))
                .join(jsonMessage, webSocketSession);
    }

    private String extractRoomId(JsonObject jsonMessage) {
        return jsonMessage.get(ROOM).getAsString();
    }

    private void leaveRoom(UserSession userSession) throws IOException {
        WebRTCRoom webRTCroom = webRTCRoomManager.getRoom(userSession.getRoomName());
        webRTCroom.leave(userSession);

        if (webRTCroom.getParticipants().isEmpty()) {
            webRTCRoomManager.removeRoom(webRTCroom);
        }
    }
}
