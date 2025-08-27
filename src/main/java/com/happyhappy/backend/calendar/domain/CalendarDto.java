package com.happyhappy.backend.calendar.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.happyhappy.backend.calendar.enums.TimeFormat;
import com.happyhappy.backend.calendar.enums.WeekStartDay;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CalendarDto {

    @Getter
    @Builder
    public static class CalendarResponse {

        private Long calendarId;
        private UUID memberId;

        // 환경설정 필드
        private WeekStartDay weekStartDay;
        private TimeFormat timeFormat;
        private RegionInfo aiSearchRegion;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateA;

        public static CalendarResponse fromEntity(Calendar calendar) {

            RegionInfo regionInfo = null;
            if (calendar.hasAiRegion()) {
                regionInfo = RegionInfo.builder()
                        .sidoCode(calendar.getAiRegionSidoCode())
                        .sidoName(calendar.getAiRegionSidoName())
                        .sigunguCode(calendar.getAiRegionSigunguCode())
                        .sigunguName(calendar.getAiRegionSigunguName())
                        .build();
            }

            return CalendarResponse.builder()
                    .calendarId(calendar.getCalendarId())
                    .memberId(calendar.getMember().getMemberId())
                    .weekStartDay(calendar.getWeekStartDay())
                    .timeFormat(calendar.getTimeFormat())
                    .aiSearchRegion(regionInfo)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MonthlyCalendarResponse {

        private CalendarResponse calendar;
        private int year;
        private int month;
        private Set<LocalDate> eventDate; // 일정이 있는 날짜들 캘린더 ui 에 점 표시용 ( 수정 )

        public static MonthlyCalendarResponse of(Calendar calendar, int year, int month,
                Set<LocalDate> eventDate) {
            return MonthlyCalendarResponse.builder()
                    .calendar(CalendarResponse.fromEntity(calendar))
                    .year(year)
                    .month(month)
                    .eventDate(eventDate)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SettingsResponse {

        private Long calendarId;
        private WeekStartDay weekStartDay;
        private TimeFormat timeFormat;
        private RegionInfo aiSearchRegion;

        public static SettingsResponse fromEntity(Calendar calendar) {

            RegionInfo regionInfo = null; // default

            if (calendar.hasAiRegion()) {
                regionInfo = RegionInfo.builder()
                        .sidoCode(calendar.getAiRegionSidoCode())
                        .sidoName(calendar.getAiRegionSidoName())
                        .sigunguCode(calendar.getAiRegionSigunguCode())
                        .sigunguName(calendar.getAiRegionSigunguName())
                        .build();
            }

            return SettingsResponse.builder()
                    .calendarId(calendar.getCalendarId())
                    .weekStartDay(calendar.getWeekStartDay())
                    .timeFormat(calendar.getTimeFormat())
                    .aiSearchRegion(regionInfo)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class WeekStartDayRequest {

        private WeekStartDay weekStartDay;
    }

    @Getter
    @NoArgsConstructor
    public static class TimeFormatRequest {

        private TimeFormat timeFormat;
    }

    @Getter
    @NoArgsConstructor
    public static class AiSearchRegionRequest {

        private String sidoCode;
        private String sigunguCode;
    }

    @Getter
    @Builder
    public static class RegionInfo {

        private String sidoCode;
        private String sidoName;
        private String sigunguCode;
        private String sigunguName;
    }

    @Getter
    @AllArgsConstructor
    public static class SidoInfo {

        private String sidoCode;
        private String sidoName;
    }

    @Getter
    @AllArgsConstructor
    public static class SigunguInfo {

        private String sigunguCode;
        private String sigunguName;
    }

}
