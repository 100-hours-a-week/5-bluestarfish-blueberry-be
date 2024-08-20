package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.dto.ChatDto;
import java.util.List;

public interface ChatService {
    ChatDto saveChat(Long roomId, ChatDto chatDto);


    List<ChatDto> listChats(Long roomId);
}
