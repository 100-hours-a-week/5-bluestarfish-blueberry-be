package com.bluestarfish.blueberry.notification.dto;

import com.bluestarfish.blueberry.notification.enumeration.NotiStatus;
import com.bluestarfish.blueberry.notification.enumeration.NotiType;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.entity.User;

public class FriendResponseDto {
    User sender;
    User receiver;
    NotiType notiType;
    NotiStatus notiStatus;
    UserResponse friend;


}
