package com.bluestarfish.blueberry.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebRTCRoomManager {
    private final ConcurrentMap<String, WebRTCRoom> rooms = new ConcurrentHashMap<>();
    private final KurentoClient kurento;

    public WebRTCRoom getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId))
                .orElseGet(() -> {
                    log.info("'{}'번 방이 존재하지 않습니다. 새로운 방을 생성합니다.", roomId);
                    WebRTCRoom newRoom = new WebRTCRoom(roomId, kurento.createMediaPipeline());
                    rooms.put(roomId, newRoom);

                    return newRoom;
                });
    }

    public void removeRoom(WebRTCRoom webRTCRoom) {
        rooms.remove(webRTCRoom.getRoomId());
        webRTCRoom.close();
        log.info(" '{}'번 방 삭제", webRTCRoom.getRoomId());
    }

}
