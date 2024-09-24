package com.bluestarfish.blueberry.webrtc.presentation;

import com.bluestarfish.blueberry.webrtc.presentation.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageHandlerMapper {
    private final Map<String, MessageHandler> handlers;

    public Optional<MessageHandler> findHandler(String messageId) {
        return Optional.ofNullable(handlers.get(messageId));
    }
}
