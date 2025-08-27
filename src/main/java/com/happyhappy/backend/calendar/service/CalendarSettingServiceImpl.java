package com.happyhappy.backend.calendar.service;


import com.happyhappy.backend.calendar.domain.Calendar;
import com.happyhappy.backend.calendar.domain.CalendarDto.SettingsResponse;
import com.happyhappy.backend.calendar.enums.TimeFormat;
import com.happyhappy.backend.calendar.enums.WeekStartDay;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarNotFoundException;
import com.happyhappy.backend.calendar.repository.CalendarRepository;
import com.happyhappy.backend.region.domain.Region;
import com.happyhappy.backend.region.service.RegionService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarSettingServiceImpl implements CalendarSettingService {

    private final CalendarRepository calendarRepository;
    private final RegionService regionService;

    @Override
    public SettingsResponse getSettings(Long calendarId) {
        Calendar calendar = findCalendarById(calendarId);
        return SettingsResponse.fromEntity(calendar);
    }

    @Override
    @Transactional
    public void updateWeekStartDay(Long calendarId, WeekStartDay weekStartDay) {
        updateCalendarSetting(calendarId, calendar -> calendar.updateWeekStartDay(weekStartDay));
    }

    @Override
    @Transactional
    public void updateTimeFormat(Long calendarId, TimeFormat timeFormat) {
        updateCalendarSetting(calendarId,
                calendar -> calendar.updateTimeFormat(timeFormat));
    }

    @Override
    @Transactional
    public void updateAiSearchRegion(Long calendarId, String sidoCode, String sigunguCode) {
        Calendar calendar = findCalendarById(calendarId);

        // 둘 다 비어있음 -> 지역 설정 해제
        if (isEmptyString(sidoCode) && isEmptyString(sigunguCode)) {
            calendar.clearAiRegion();
            return;
        }

        // 시도만 있음 -> 시도 전체 선택 ( 시군구 코드 0000 )
        if (!isEmptyString(sidoCode) && isEmptyString(sigunguCode)) {
            Region region = regionService.findRegionByCode(sidoCode, "0000")
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("해당 시도의 전체 지역 설정을 찾을 수 없습니다. [시도: %s]", sidoCode
                            )));
            calendar.updateAiRegion(region);
            return;
        }

        if (!isEmptyString(sidoCode) && !isEmptyString(sigunguCode)) {
            Region region = regionService.findRegionByCode(sidoCode, sigunguCode)
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("해당 지역을 찾을 수 없습니다. [시도: %s][시군구: %s]", sidoCode,
                                    sigunguCode)
                    ));
            calendar.updateAiRegion(region);
            return;
        }

        // 시도 없이 시군구만 있을 시
        throw new IllegalCallerException("시군구를 선택하려면 시도를 먼저 선택해야 합니다.");
    }

    /**
     * 캘린더 설정 업데이트 공통 패턴
     */

    private void updateCalendarSetting(Long calendarId, Consumer<Calendar> updateAction) {
        Calendar calendar = findCalendarById(calendarId);
        updateAction.accept(calendar);
    }

    private Calendar findCalendarById(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarNotFoundException(calendarId));
    }

    private boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

}
