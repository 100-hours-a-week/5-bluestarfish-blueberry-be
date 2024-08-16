package com.bluestarfish.blueberry.chat.controller;

import com.bluestarfish.blueberry.chat.entity.Chat;
import com.bluestarfish.blueberry.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/{roomId}/chats")
    @SendTo("/rooms/{roomId}")
    public Chat sendMessage(@DestinationVariable("roomId") Long roomId, Chat chatting) {
        Chat chat = chatService.saveChat(roomId, chatting.getSenderId(), chatting.getMessage());

        return chat;
    }
}
