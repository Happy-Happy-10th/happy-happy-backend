package com.happyhappy.backend.calendar.dto;

import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import com.happyhappy.backend.calendar.enums.EventColor;
import com.happyhappy.backend.calendar.enums.RepeatType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {

    private Response response;

    public List<EventResponse> toEventResponses() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Optional.ofNullable(response)
                .map(Response::getBody)
                .map(Body::getItems)
                .map(Items::getItem)
                .orElse(List.of())
                .stream()
                .map(item -> {
                    LocalDate date = LocalDate.parse(item.getLocdate(), formatter);
                    return EventDto.EventResponse.builder()
                            .title(item.getDateName())
                            .allDay(true)
                            .startDate(date.atStartOfDay())
                            .endDate(date.plusDays(1).atStartOfDay())
                            .repeatCycle(RepeatType.YEAR)
                            .color(EventColor.YOTEYO_RED)
                            .isHoliday(true)
                            .isPending(false)
                            .isYoteyo(false)
                            .build();
                })
                .toList();
    }

    @Getter
    @Setter
    public static class Response {

        private Body body;
    }

    @Getter
    @Setter
    public static class Body {

        private Items items;
    }

    @Getter
    @Setter
    public static class Items {

        private List<HolidayItem> item;
    }

    @Getter
    @Setter
    public static class HolidayItem {

        private String dateName;
        private String locdate;
    }
}
