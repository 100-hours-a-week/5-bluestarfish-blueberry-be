package com.bluestarfish.blueberry.chat.service;

import com.bluestarfish.blueberry.chat.entity.Chat;
import com.bluestarfish.blueberry.chat.repository.ChatRepository;
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
    public Chat saveChat(Long roomId, Long senderId, String message) {
        Chat chat = Chat.builder()
                .roomId(roomId)
                .senderId(senderId)
                .message(message)
                .build();

        return chatRepository.save(chat);
    }
}
