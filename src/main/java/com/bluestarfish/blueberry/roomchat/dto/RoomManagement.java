package com.bluestarfish.blueberry.roomchat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomManagement {
    private Long userId;
    private Long roomId;
    private boolean micEnabled;
    private boolean camEnabled;
    private boolean speakerEnabled;

//    public UserRoom toEntity(User user, Room room) {
//        return UserRoom.builder()
//                .user(user)
//                .room(room)
//                .micEnabled(micEnabled)
//                .camEnabled(camEnabled)
//                .speakerEnabled(speakerEnabled)
//                .build();
//    }
}
