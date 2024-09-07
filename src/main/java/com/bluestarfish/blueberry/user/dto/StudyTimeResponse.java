package com.bluestarfish.blueberry.user.dto;

import com.bluestarfish.blueberry.user.entity.StudyTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@Builder
public class StudyTimeResponse {
    private Long id;
    private Time time;
    private UserResponse userResponse;

    public static StudyTimeResponse from(StudyTime studyTime) {
        return StudyTimeResponse.builder()
                .id(studyTime.getId())
                .time(studyTime.getTime())
                .userResponse(UserResponse.from(studyTime.getUser()))
                .build();
    }
}
