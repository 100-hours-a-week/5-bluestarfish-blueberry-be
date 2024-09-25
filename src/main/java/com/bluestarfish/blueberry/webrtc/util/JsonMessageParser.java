package com.bluestarfish.blueberry.webrtc.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;

@Slf4j
public class JsonMessageParser {
    private static final Gson gson = new Gson();

    public static JsonObject convertToJsonObject(TextMessage textMessage) {
        return gson.fromJson(textMessage.getPayload(), JsonObject.class);
    }

    public static String extractMessageId(JsonObject jsonMessage) {
        return extractFromMessage(jsonMessage, SOCKET_MESSAGE_ID);
    }

    public static String extractSdpOffer(JsonObject jsonMessage) {
        return extractFromMessage(jsonMessage, SDP_OFFER);
    }

    public static String extractRoomId(JsonObject jsonMessage) {
        return extractFromMessage(jsonMessage, ROOM);
    }

    public static JsonObject extractCandidate(JsonObject jsonMessage) {
        return jsonMessage.get(CANDIDATE).getAsJsonObject();
    }

    private static String extractFromMessage(JsonObject jsonMessage, String key) {
        return jsonMessage.get(key).getAsString();
    }
}
