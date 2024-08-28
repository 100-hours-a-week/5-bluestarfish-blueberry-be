package com.bluestarfish.blueberry.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebRTCRoomManager {
    private final ConcurrentMap<String, WebRTCRoom> rooms = new ConcurrentHashMap<>();
    private final KurentoClient kurento;

    public WebRTCRoom getRoom(String roomName) {
        log.debug("Searching for room {}", roomName);
        WebRTCRoom room = rooms.get(roomName);

        if (room == null) {
            log.debug("Room {} not existent. Will create now!", roomName);
            room = new WebRTCRoom(roomName, kurento.createMediaPipeline());
            rooms.put(roomName, room);
        }
        log.debug("Room {} found!", roomName);

        return room;
    }

    /**
     * Removes a room from the list of available rooms.
     *
     * @param room the room to be removed
     */
    public void removeRoom(WebRTCRoom room) {
        this.rooms.remove(room.getName());
        room.close();
        log.info("Room {} removed and closed", room.getName());
    }

}
