package com.bluestarfish.blueberry.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FoundUserResponse {
    private Long id;
    private String profileImage;
    private String nickname;
    private Time time;
    private Boolean isFriend;
}
