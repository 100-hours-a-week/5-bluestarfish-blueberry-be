package com.bluestarfish.blueberry.roomchat.dto;

import com.bluestarfish.blueberry.roomchat.entity.Chat;
import com.bluestarfish.blueberry.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private String id;
    private Long roomId;
    private Long senderId;
    private String message;
    private String senderNickname;
    private String senderProfileImage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static ChatDto from(Chat chat, User userInfo) {
        return ChatDto.builder()
                .id(chat.getId())
                .roomId(chat.getRoomId())
                .senderId(chat.getSenderId())
                .message(chat.getMessage())
                .senderNickname(userInfo.getNickname())
                .senderProfileImage("temporary.url")
                .createdAt(chat.getCreatedAt())
                .build();
    }


}
