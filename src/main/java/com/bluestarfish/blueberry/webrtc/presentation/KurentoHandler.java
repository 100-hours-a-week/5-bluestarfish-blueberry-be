package com.bluestarfish.blueberry.webrtc.presentation;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.commons.exception.KurentoException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.MESSAGE;
import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.SOCKET_MESSAGE_ID;
import static com.bluestarfish.blueberry.webrtc.util.JsonMessageParser.convertToJsonObject;
import static com.bluestarfish.blueberry.webrtc.util.JsonMessageParser.extractMessageId;

@Slf4j
@Component
@RequiredArgsConstructor
public class KurentoHandler extends TextWebSocketHandler {

    private final MessageHandlerAdapter messageHandlerAdapter;

    @Override
    public void handleTextMessage(
            @Nullable final WebSocketSession webSocketSession,
            @Nullable final TextMessage textMessage
    ) {
        try {
            Optional.ofNullable(textMessage).orElseThrow(
                    () -> new KurentoException("WebSocket message is null")
            );
        } catch (KurentoException e) {
            log.error("KurentoException occurred: {}", e.getMessage());
            sendErrorMessage(webSocketSession, e.getMessage());
            return;
        }

        JsonObject jsonMessage = convertToJsonObject(textMessage);
        String messageId = extractMessageId(jsonMessage);

        messageHandlerAdapter.handleMessage(
                messageId,
                jsonMessage,
                webSocketSession
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        messageHandlerAdapter.handleMessage(
                "afterConnectionClosed",
                null,
                webSocketSession
        );
    }

    private void sendErrorMessage(
            @Nullable WebSocketSession webSocketSession,
            String errorMessage
    ) {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty(SOCKET_MESSAGE_ID, "ERROR");
                errorResponse.addProperty(MESSAGE, errorMessage);
                webSocketSession.sendMessage(new TextMessage(errorResponse.toString()));
            } catch (IOException e) {
                log.error("Failed to send error message to client: {}", e.getMessage());
            }
        }
    }
}
