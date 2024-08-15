package com.bluestarfish.blueberry.chat.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chats")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    private Long id;

    @Field("room_id")
    private Long roomId;

    @Field("sender_id")
    private Long senderId;

    private String message;

    @Field("created_at")
    private LocalDateTime createdAt;
}
