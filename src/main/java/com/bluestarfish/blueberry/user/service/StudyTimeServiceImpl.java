package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.exception.CustomException;
import com.bluestarfish.blueberry.exception.ExceptionDomain;
import com.bluestarfish.blueberry.user.dto.ChartDataResponse;
import com.bluestarfish.blueberry.user.entity.StudyTime;
import com.bluestarfish.blueberry.user.repository.StudyTimeRepository;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyTimeServiceImpl implements StudyTimeService {
    private final StudyTimeRepository studyTimeRepository;

    @Override
    public List<ChartDataResponse> getChartData(Long userId) {
        List<ChartDataResponse> weeklyData = getWeeklyStudyTime(userId);
        weeklyData.addAll(getMonthlyStudyTime(userId));
        return weeklyData;
    }

    private List<ChartDataResponse> getWeeklyStudyTime(Long userId) {
        List<ChartDataResponse> chartDataResponses = new ArrayList<>();

        // 최근 7주에 대한 데이터
        for(int i=0; i<7; i++) {
            LocalDate endDate = LocalDate.now().minusWeeks(i);
            LocalDate startDate = endDate.minusWeeks(1);

            // QueryDSL을 사용하여 데이터 조회
            List<StudyTime> studyTimes = studyTimeRepository.findStudyTimesBetweenDates(userId, startDate, endDate);
            if(studyTimes.isEmpty()) {
                throw new CustomException("No Study Time Data for User. userId : " + userId, ExceptionDomain.USER, HttpStatus.NOT_FOUND);
            }

            Time totalTime = calculateTotalTime(studyTimes);

            ChartDataResponse response = ChartDataResponse.builder()
                    .type("weekly")
                    .no(7 - i)
                    .time(totalTime)
                    .build();

            chartDataResponses.add(response);
        }
        return chartDataResponses;
    }

    private List<ChartDataResponse> getMonthlyStudyTime(Long userId) {
        List<ChartDataResponse> chartDataResponses = new ArrayList<>();

        // 최근 12개월에 대한 데이터
        for (int i = 0; i < 12; i++) {
            LocalDate endDate = LocalDate.now().minusMonths(i);
            LocalDate startDate = endDate.minusMonths(1);

            // QueryDSL을 사용하여 데이터 조회
            List<StudyTime> studyTimes = studyTimeRepository.findStudyTimesBetweenDates(userId, startDate, endDate);
            if(studyTimes.isEmpty()) {
                throw new CustomException("No User Existed. userId : " + userId, ExceptionDomain.USER, HttpStatus.NOT_FOUND);
            }

            Time totalTime = calculateTotalTime(studyTimes);

            ChartDataResponse response = ChartDataResponse.builder()
                    .type("monthly")
                    .no(12 - i)  // 최근 달부터 번호 매김
                    .time(totalTime)
                    .build();

            chartDataResponses.add(response);
        }

        return chartDataResponses;
    }

    private Time calculateTotalTime(List<StudyTime> studyTimes) {
        long totalMinutes = 0;
        for(StudyTime studyTime : studyTimes) {
            totalMinutes += studyTime.getTime().toLocalTime().getHour() * 60
                    + studyTime.getTime().toLocalTime().getMinute();
        }
        return convertMinutesToTime(totalMinutes);
    }

    private Time convertMinutesToTime(long totalMinutes) {
        int hours = (int) totalMinutes / 60;
        int minutes = (int) totalMinutes % 60;
        return Time.valueOf(String.format("%02d:%02d:00", hours, minutes));
    }
}
