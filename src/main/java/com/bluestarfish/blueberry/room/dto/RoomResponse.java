package com.bluestarfish.blueberry.room.dto;

import com.bluestarfish.blueberry.room.entity.Room;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomResponse {
    private Long id;
    private String title;
    private int maxUsers;
    private boolean isCamEnabled;
    private String thumbnail;
    private String description;
    private int memberNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public static RoomResponse from(Room room, int memberNumber) {
        return RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .maxUsers(room.getMaxUsers())
                .isCamEnabled(room.isCamEnabled())
                .thumbnail(room.getThumbnail())
                .description(room.getDescription())
                .memberNumber(memberNumber)
                .createdAt(room.getCreatedAt())
                .deletedAt(room.getDeletedAt())
                .build();
    }
}
