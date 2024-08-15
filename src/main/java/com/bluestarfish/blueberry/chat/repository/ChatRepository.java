package com.bluestarfish.blueberry.chat.repository;

import com.bluestarfish.blueberry.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat, Long> {
}
