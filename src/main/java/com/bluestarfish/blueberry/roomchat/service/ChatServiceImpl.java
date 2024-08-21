package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import com.bluestarfish.blueberry.roomchat.entity.Chat;
import com.bluestarfish.blueberry.roomchat.repository.ChatRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public ChatDto saveChat(Long roomId, ChatDto chatDto) {
        Chat chat = Chat.builder()
                .roomId(roomId)
                .senderId(chatDto.getSenderId())
                .message(chatDto.getMessage())
                .build();

        Chat savedChat = chatRepository.save(chat);
        return ChatDto.from(savedChat);
    }

    @Override
    public List<ChatDto> listChats(Long roomId) {
        return chatRepository.findAllByRoomId(roomId).stream()
                .map(ChatDto::from)
                .toList();

    }

}
