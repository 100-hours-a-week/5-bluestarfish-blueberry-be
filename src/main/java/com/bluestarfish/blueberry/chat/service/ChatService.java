package com.bluestarfish.blueberry.chat.service;

import com.bluestarfish.blueberry.chat.entity.Chat;

public interface ChatService {
    Chat saveChat(Long roomId, Long senderId, String message);
}
