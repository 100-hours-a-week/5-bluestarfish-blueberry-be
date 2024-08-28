package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import com.bluestarfish.blueberry.roomchat.entity.Chat;
import com.bluestarfish.blueberry.roomchat.repository.ChatRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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

        Optional<User> chatUser = userRepository.findById(chatDto.getSenderId());

        Chat savedChat = chatRepository.save(chat);

        return ChatDto.from(savedChat, chatUser.get());
    }

    @Override
    public List<ChatDto> listChats(Long roomId) {
        return chatRepository.findAllByRoomId(roomId).stream()
                .map(chat -> {
                    Optional<User> chatUser = userRepository.findById(chat.getSenderId());
                    return ChatDto.from(chat, chatUser.get());
                })
                .toList();
    }

}
