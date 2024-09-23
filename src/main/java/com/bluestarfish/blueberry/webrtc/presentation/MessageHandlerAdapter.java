package com.bluestarfish.blueberry.webrtc.presentation;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerAdapter {
    private final MessageHandlerMapper messageHandlerMapper;

    public void handleMessage(
            String messageId,
            JsonObject jsonMessage,
            WebSocketSession webSocketSession
    ) {
        messageHandlerMapper.findHandler(messageId)
                .ifPresentOrElse(messageHandler -> {
                            try {
                                messageHandler.handleMessage(jsonMessage, webSocketSession);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        () -> log.error("No matching message handler found for message ID: {}", messageId)
                );
    }
}
