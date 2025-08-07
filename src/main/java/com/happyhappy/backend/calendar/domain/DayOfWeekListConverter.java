package com.happyhappy.backend.calendar.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class DayOfWeekListConverter implements AttributeConverter<List<DayOfWeek>, String> {

    @Override
    public String convertToDatabaseColumn(List<DayOfWeek> dayOfWeeks) {
        if (dayOfWeeks == null || dayOfWeeks.isEmpty()) {
            return "";
        }
        return dayOfWeeks.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<DayOfWeek> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of(); // 빈 리스트 반환
        }
        return Arrays.stream(dbData.split(","))
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toList());
    }
}
