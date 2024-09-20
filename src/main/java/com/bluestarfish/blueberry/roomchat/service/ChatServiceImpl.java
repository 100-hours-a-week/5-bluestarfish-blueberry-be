package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import com.bluestarfish.blueberry.roomchat.entity.Chat;
import com.bluestarfish.blueberry.roomchat.repository.ChatRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChatDto saveChat(Long roomId, ChatDto chatDto) {
        Chat chat = Chat.builder()
                .roomId(roomId)
                .senderId(chatDto.getSenderId())
                .message(chatDto.getMessage())
                .build();

        User chatUser = userRepository.findById(chatDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat savedChat = chatRepository.save(chat);

        return ChatDto.from(savedChat, chatUser);
    }

    @Override
    public List<ChatDto> listChats(Long roomId) {
        return chatRepository.findAllByRoomId(roomId).stream()
                .map(chat -> {
                    User chatUser = userRepository.findById(chat.getSenderId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    return ChatDto.from(chat, chatUser);
                })
                .toList();
    }

}
