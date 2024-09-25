package com.bluestarfish.blueberry.webrtc.application;

import com.bluestarfish.blueberry.user.repository.UserRepository;
import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import com.bluestarfish.blueberry.webrtc.domain.WebRTCRoom;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bluestarfish.blueberry.webrtc.util.JsonMessageParser.extractRoomId;
import static com.bluestarfish.blueberry.webrtc.util.JsonMessageParser.extractSdpOffer;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebRTCRoomManager {
    private final KurentoClient kurento;
    private final UserRepository userRepository;
    private final ConcurrentMap<String, WebRTCRoom> rooms = new ConcurrentHashMap<>();

    public UserSession join(JsonObject jsonMessage, WebSocketSession webSocketSession) throws IOException {
        WebRTCRoom webRTCRoom = getRoom(extractRoomId(jsonMessage));
        return webRTCRoom.join(jsonMessage, webSocketSession);
    }

    public WebRTCRoom getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId))
                .orElseGet(() -> {
                    log.info("'{}'번 방이 존재하지 않습니다. 새로운 방을 생성합니다.", roomId);
                    WebRTCRoom newRoom = new WebRTCRoom(roomId, kurento.createMediaPipeline(), userRepository);
                    rooms.put(roomId, newRoom);

                    return newRoom;
                });
    }

    public void removeRoom(WebRTCRoom webRTCRoom) {
        rooms.remove(webRTCRoom.getRoomId());
        webRTCRoom.close();
        log.info(" '{}'번 방 삭제", webRTCRoom.getRoomId());
    }

    public void receiveVideoFrom(JsonObject jsonMessage, UserSession userSession, UserSession sender) throws IOException {
        userSession.receiveVideoFrom(sender, extractSdpOffer(jsonMessage));
    }


    public void leaveRoom(UserSession userSession) throws IOException {
        rooms.get(userSession.getRoomName()).leave(userSession);
    }

    public void sendCamControl(JsonObject jsonMessage, UserSession userSession) throws IOException {
        WebRTCRoom webRTCRoom = rooms.get(userSession.getRoomName());
        webRTCRoom.sendCamControl(jsonMessage, userSession);
    }

    public void sendMicControl(JsonObject jsonMessage, UserSession userSession) {
        WebRTCRoom webRTCRoom = rooms.get(userSession.getRoomName());
        webRTCRoom.sendMicControl(jsonMessage, userSession);
    }

    private WebRTCRoom createNewRoom(String roomId) {
        return new WebRTCRoom(roomId, kurento.createMediaPipeline(), userRepository);
    }
}
