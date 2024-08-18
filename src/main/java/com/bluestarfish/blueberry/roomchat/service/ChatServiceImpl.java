package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.entity.Chat;
import com.bluestarfish.blueberry.roomchat.repository.ChatRepository;
import java.time.LocalDateTime;
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
    public Chat saveChat(Long roomId, Long senderId, String message) {
        Chat chat = Chat.builder()
                .roomId(roomId)
                .senderId(senderId)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    @Override
    public List<Chat> listChats(Long roomId) {
        return chatRepository.findAllByRoomId(roomId);
    }

}
