package com.bluestarfish.blueberry.roomchat.controller;

import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import com.bluestarfish.blueberry.common.handler.ResponseHandler;
import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import com.bluestarfish.blueberry.roomchat.service.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

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

}
