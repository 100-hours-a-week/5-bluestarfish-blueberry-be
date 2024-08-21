package com.bluestarfish.blueberry.roomchat.repository;

import com.bluestarfish.blueberry.roomchat.entity.Chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findAllByRoomId(Long roomId);
}
