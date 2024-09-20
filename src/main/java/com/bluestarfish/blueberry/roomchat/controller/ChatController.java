package com.bluestarfish.blueberry.roomchat.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.handler.ResponseHandler;
import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import com.bluestarfish.blueberry.roomchat.service.ChatService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Controller
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    private final Set<String> connectedUsers = new HashSet<>();

    @MessageMapping("/{roomId}/chats")
    @SendTo("/topic/rooms/{roomId}")
    public ChatDto sendMessage(@DestinationVariable("roomId") Long roomId, ChatDto chatDto) {
        return chatService.saveChat(roomId, chatDto);
    }

    @GetMapping("/rooms/{roomId}/chats")
    public ApiSuccessResponse<?> listChats(@PathVariable("roomId") Long roomId) {
        List<ChatDto> chat = chatService.listChats(roomId);
        return ResponseHandler.handleSuccessResponse(chat, HttpStatus.OK);
    }

    @EventListener
    public void websocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        connectedUsers.add(sessionId);

        simpMessagingTemplate.convertAndSend("/topic/rooms/");
    }

}
