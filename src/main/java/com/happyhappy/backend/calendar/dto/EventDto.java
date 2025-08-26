package com.happyhappy.backend.calendar.dto;

import com.happyhappy.backend.calendar.domain.Event;
import com.happyhappy.backend.calendar.enums.EventColor;
import com.happyhappy.backend.calendar.enums.RepeatType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class EventDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRequest {

        private Long calendarId;
        private String title;
        private boolean allDay;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private RepeatType repeatCycle;
        private EventColor color;
        private String locate;
        private String memo;
        private boolean isHoliday;
        private boolean isPending;
        private boolean isYoteyo;

        public Event toEntity() {
            String repeatCycleCode = null;
            if (repeatCycle != null) {
                try {
                    repeatCycleCode = repeatCycle.getCode();
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("유효하지 않은 반복 주기 값입니다: " + repeatCycle);
                }
            }

            return Event.builder()
                    .title(title)
                    .allDay(allDay)
                    .startDate(startDate)
                    .endDate(endDate)
                    .repeatCycle(repeatCycleCode)
                    .color(color)
                    .locate(locate)
                    .memo(memo)
                    .isHoliday(isHoliday)
                    .isPending(isPending)
                    .isYoteyo(isYoteyo)
                    .build();
        }

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventResponse {

        private Long id;
        private Long calendarId;
        private String title;
        private boolean allDay;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private RepeatType repeatCycle;
        private EventColor color;
        private String locate;
        private String memo;
        private boolean isHoliday;
        private boolean isPending;
        private boolean isYoteyo;


        public static EventResponse fromEntity(Event entity) {
            return EventResponse.builder()
                    .id(entity.getEventId())
                    .calendarId(entity.getCalendar().getCalendarId())
                    .title(entity.getTitle())
                    .allDay(entity.isAllDay())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .repeatCycle(entity.getRepeatCycle() != null ? RepeatType.fromCode(
                            entity.getRepeatCycle())
                            : null)
                    .color(entity.getColor())
                    .locate(entity.getLocate())
                    .memo(entity.getMemo())
                    .isHoliday(entity.isHoliday())
                    .isPending(entity.isPending())
                    .isYoteyo(entity.isYoteyo())
                    .build();
        }
    }
}
