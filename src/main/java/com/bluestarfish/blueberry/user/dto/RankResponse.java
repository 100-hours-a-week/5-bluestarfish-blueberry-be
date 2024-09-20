package com.bluestarfish.blueberry.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Time;

@Getter
@Builder
public class RankResponse {
    private Integer rank;
    private String nickname;
    private Time time;
}
