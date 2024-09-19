package com.bluestarfish.blueberry.room.dto;

import com.bluestarfish.blueberry.room.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, max = 15, message = "Title must be between 5 and 20 characters")
    private String title;

    private int maxUsers;
    private boolean isCamEnabled;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 4, max = 20, message = "Password must be between 5 and 10 characters")
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
