package com.bluestarfish.blueberry.roomchat.dto;

import com.bluestarfish.blueberry.common.entity.UserRoom;
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
public class RoomManagementDto {
    private Long userId;
    private Long roomId;
    private boolean micEnabled;
    private boolean camEnabled;
    private boolean speakerEnabled;

    public static RoomManagementDto from(UserRoom userRoom) {
        return RoomManagementDto.builder()
                .userId(userRoom.getUser().getId())
                .roomId(userRoom.getRoom().getId())
                .micEnabled(userRoom.isMicEnabled())
                .camEnabled(userRoom.isCamEnabled())
                .speakerEnabled(userRoom.isSpeakerEnabled())
                .build();
    }
}
