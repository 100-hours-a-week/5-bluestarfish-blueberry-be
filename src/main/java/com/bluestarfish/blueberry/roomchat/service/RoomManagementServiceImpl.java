package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.common.repository.UserRoomRepository;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.roomchat.dto.RoomManagementDto;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoomManagementServiceImpl implements RoomManagementService {

    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomManagementServiceImpl(UserRoomRepository userRoomRepository, UserRepository userRepository) {
        this.userRoomRepository = userRoomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoomManagementDto roomControlUpdate(Long roomId, RoomManagementDto roomManagementDto) {
        UserRoom userRoom = (UserRoom) userRoomRepository.findByRoomIdAndUserId(roomId, roomManagementDto.getUserId())
                .orElseThrow(() -> new RoomException(
                        "Room not found with id: " + roomId + " for user id: " + roomManagementDto.getUserId(),
                        HttpStatus.NOT_FOUND));

        userRoom.setCamEnabled(roomManagementDto.isCamEnabled());
        userRoom.setMicEnabled(roomManagementDto.isMicEnabled());
        userRoom.setSpeakerEnabled(roomManagementDto.isSpeakerEnabled());

        UserRoom updatedUserRoom = userRoomRepository.save(userRoom);

        return RoomManagementDto.from(updatedUserRoom);
    }
}
