package com.bluestarfish.blueberry.roomchat.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chats")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private String id;

    @Field("room_id")
    @Indexed
    private Long roomId;

    @Field("sender_id")
    private Long senderId;

    private String message;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Builder
    public Chat(Long roomId, Long senderId, String message) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
