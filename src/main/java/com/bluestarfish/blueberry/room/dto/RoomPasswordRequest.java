package com.bluestarfish.blueberry.room.dto;

import lombok.Getter;

@Getter
public class RoomPasswordRequest {
    private Long roomId;
    private String password;
}
