package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.Calendar;
import com.happyhappy.backend.calendar.domain.CalendarDto.CalendarResponse;
import com.happyhappy.backend.calendar.domain.CalendarDto.MonthlyCalendarResponse;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarNotFoundException;
import com.happyhappy.backend.calendar.repository.CalendarRepository;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;

    // TODO : EventService 추가
    // private final EventService eventService;

    @Override
    public MonthlyCalendarResponse getMonthlyCalendar(Long calendarId, int year, int month) {
        Calendar calendar = findCalendarByCalendarId(calendarId);
        // TODO : EventService 연동 후 실제 이벤트 날짜 조회
        // 임시로 빈 set 반환 ( Event 기능 구현 전까지 )
        Set<LocalDate> eventDates = new HashSet<>();

        return MonthlyCalendarResponse.of(calendar, year, month, eventDates);
    }

    @Override
    public MonthlyCalendarResponse getCurrentMonthCalendar(Long calendarId) {
        LocalDate now = LocalDate.now();
        return getMonthlyCalendar(calendarId, now.getYear(), now.getMonthValue());
    }

    @Override
    public CalendarResponse getCalendarByMemberId(UUID memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. ID: " + memberId));

        Calendar calendar = member.getCalendar();
        if (calendar == null) {
            throw new CalendarNotFoundException("해당 회원의 캘린더가 없습니다. MemberID: " + memberId);
        }
        return CalendarResponse.fromEntity(calendar);
    }

    private Calendar findCalendarByCalendarId(Long calendarId) {
        return calendarRepository.findByCalendarId(calendarId)
                .orElseThrow(() -> new CalendarNotFoundException(calendarId));
    }
}
