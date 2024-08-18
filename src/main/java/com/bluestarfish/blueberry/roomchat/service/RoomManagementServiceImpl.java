package com.bluestarfish.blueberry.roomchat.service;

import com.bluestarfish.blueberry.common.repository.UserRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomManagementServiceImpl implements RoomManagementService {

    private final UserRoomRepository userRoomRepository;

    @Autowired
    public RoomManagementServiceImpl(UserRoomRepository userRoomRepository) {
        this.userRoomRepository = userRoomRepository;
    }


}
