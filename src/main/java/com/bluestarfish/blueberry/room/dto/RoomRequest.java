package com.bluestarfish.blueberry.room.dto;

import com.bluestarfish.blueberry.room.entity.Room;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private Long id;
    private Long userId;
    private String title;
    private int maxUsers;
    private boolean isCamEnabled;
    private String password;
    private MultipartFile thumbnail;
    private String description;

    public Room toEntity(String thumbnailUrl) {
        return Room.builder()
                .title(title)
                .maxUsers(maxUsers)
                .isCamEnabled(isCamEnabled)
                .password(password)
                .thumbnail(thumbnailUrl)
                .description(description)
                .build();
    }
}
