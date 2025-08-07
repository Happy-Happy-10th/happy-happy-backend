package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.dto.ApiResponse;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class HolidayApiService {

    private final WebClient webClient;

    @Value("${api.holiday.serviceKey}")
    private String serviceKey;

    public List<EventResponse> getHolidays(int year, int month) {
        ApiResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getRestDeInfo")
                        .queryParam("solYear", year)
                        .queryParam("solMonth", String.format("%02d", month))
                        .queryParam("ServiceKey", serviceKey)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();

        return Optional.ofNullable(response)
                .map(ApiResponse::toEventResponses)
                .orElse(List.of());

    }
}

