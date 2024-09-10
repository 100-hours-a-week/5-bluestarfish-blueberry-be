package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.ChartDataResponse;
import java.util.List;

public interface StudyTimeService {
    List<ChartDataResponse> getChartData(Long userId);
}
