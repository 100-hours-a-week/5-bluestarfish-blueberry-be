package com.bluestarfish.blueberry.room.dto;

import com.bluestarfish.blueberry.common.dto.UserRoomResponse;
import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.room.entity.Room;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomDetailResponse {
    private Long id;
    private String title;
    private int maxUsers;
    private boolean isCamEnabled;
    private String password;
    private String thumbnail;
    private String description;
    private List<UserRoomResponse> userRooms;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public static RoomDetailResponse from(Room room, List<UserRoomResponse> userRooms) {
        return RoomDetailResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .maxUsers(room.getMaxUsers())
                .isCamEnabled(room.isCamEnabled())
                .password(room.getPassword())
                .thumbnail(room.getThumbnail())
                .description(room.getDescription())
                .userRooms(userRooms)
                .createdAt(room.getCreatedAt())
                .deletedAt(room.getDeletedAt())
                .build();
    }
}
