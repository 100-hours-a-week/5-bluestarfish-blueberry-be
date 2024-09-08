package com.bluestarfish.blueberry.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class StudyTimeUpdateRequest {
    private Time time;
}
