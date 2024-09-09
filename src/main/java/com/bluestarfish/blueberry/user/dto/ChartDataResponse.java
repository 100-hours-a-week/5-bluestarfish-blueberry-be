package com.bluestarfish.blueberry.user.dto;

import java.sql.Time;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChartDataResponse {
    String type;
    int no;
    Time time;
}
