package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.roomchat.entity.Chat;
import java.util.List;

public interface ChatService {
    Chat saveChat(Long roomId, Long senderId, String message);


    List<Chat> listChats(Long roomId);
}
