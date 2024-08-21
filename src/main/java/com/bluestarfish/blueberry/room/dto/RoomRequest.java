package com.bluestarfish.blueberry.room.dto;

import com.bluestarfish.blueberry.room.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private Long id;
    private String title;
    private int maxUsers;
    private boolean isCamEnabled;
    private String password;
    private String thumbnail;
    private String description;

    public Room toEntity() {
        return Room.builder()
                .title(title)
                .maxUsers(maxUsers)
                .isCamEnabled(isCamEnabled)
                .password(password)
                .thumbnail(thumbnail)
                .description(description)
                .build();
    }
}
