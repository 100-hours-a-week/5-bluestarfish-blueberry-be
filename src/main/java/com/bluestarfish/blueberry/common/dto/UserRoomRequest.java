package com.bluestarfish.blueberry.common.dto;

import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.user.entity.User;
import java.sql.Time;
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
public class UserRoomRequest {
    private Long id;
    private Long userId;
    private Long roomId;
    private boolean isHost;
    private boolean isActive;
    private boolean camEnabled;
    private boolean micEnabled;
    private boolean speakerEnabled;
    private Time goalTime;
    private Time dayTime;
    private String password;

    public UserRoom toEntity(User user, Room room) {
        return UserRoom.builder()
                .user(user)
                .room(room)
                .isHost(isHost)
                .isActive(isActive)
                .camEnabled(camEnabled)
                .micEnabled(micEnabled)
                .speakerEnabled(speakerEnabled)
                .goalTime(goalTime)
                .dayTime(dayTime)
                .build();
    }
}
