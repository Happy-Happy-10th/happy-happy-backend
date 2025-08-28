package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.Calendar;
import com.happyhappy.backend.calendar.domain.Event;
import com.happyhappy.backend.calendar.dto.EventDto.EventRequest;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import com.happyhappy.backend.calendar.enums.RepeatType;
import com.happyhappy.backend.calendar.exception.EventException.RepeatPeriodException;
import com.happyhappy.backend.calendar.repository.CalendarRepository;
import com.happyhappy.backend.calendar.repository.EventRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;

    @Override
    public EventResponse createEvent(EventRequest request) {
        validateDateOrder(request.getStartDate(), request.getEndDate());
        validateRepeatPeriod(request.getStartDate(), request.getEndDate(),
                request.getRepeatCycle());

        Event event = request.toEntity();
        Calendar calendar = calendarRepository.findById(request.getCalendarId())
                .orElseThrow(() -> new IllegalArgumentException("캘린더가 존재하지 않습니다."));
        event.setCalendar(calendar);

        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }


    @Override
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        validateDateOrder(request.getStartDate(), request.getEndDate());
        validateRepeatPeriod(request.getStartDate(), request.getEndDate(),
                request.getRepeatCycle());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));

        event.setTitle(request.getTitle());
        event.setAllDay(request.isAllDay());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setRepeatCycle(
                request.getRepeatCycle() != null ? request.getRepeatCycle().getCode() : null);
        event.setColor(request.getColor());
        event.setLocate(request.getLocate());
        event.setMemo(request.getMemo());
        event.setHoliday(request.isHoliday());

        Optional.ofNullable(request.getCalendarId()).ifPresent(id -> {
            Calendar calendar = calendarRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("캘린더가 없습니다."));
            event.setCalendar(calendar);
        });

        return EventResponse.fromEntity(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    private EventResponse buildOccurrence(Event m, LocalDateTime occS, LocalDateTime occE) {
        EventResponse dto = EventResponse.fromEntity(m);
        dto.setStartDate(occS);
        dto.setEndDate(occE);
        return dto;
    }

    @Override
    public List<EventResponse> getEventsByYear(Long calendarId, int year) {
        LocalDateTime periodStart = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime periodEnd = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

        List<Event> masters = eventRepository.findByCalendar_CalendarIdAndStartDateBefore(
                calendarId, periodEnd
        );

        List<EventResponse> out = new ArrayList<>();
        for (Event m : masters) {
            // 반복주기 없는 경우
            if (m.getRepeatCycle() == null) {
                if (!m.getEndDate().isBefore(periodStart) && !m.getStartDate().isAfter(periodEnd)) {
                    out.add(EventResponse.fromEntity(m));
                }
                continue;
            }
            // 반복주기 있는 경우
            RepeatType rt;
            try {
                rt = RepeatType.fromCode(m.getRepeatCycle());
            } catch (IllegalArgumentException e) {
                System.err.println(
                        "잘못된 반복주기 값: " + m.getRepeatCycle() + " (eventId=" + m.getEventId() + ")");
                continue;
            }

            LocalDateTime occS = m.getStartDate();
            LocalDateTime occE = m.getEndDate();

            // 반복주기 DAY인 경우
            if (rt == RepeatType.DAY) {
                LocalDateTime currentStart = occS;

                long minutes = java.time.Duration.between(m.getStartDate(), m.getEndDate())
                        .toMinutes();

                while (!currentStart.isAfter(occE)) {
                    LocalDateTime currentEnd = currentStart.plusMinutes(minutes);

                    if (!currentEnd.isBefore(periodStart) && !currentStart.isAfter(periodEnd)) {
                        out.add(buildOccurrence(m, currentStart, currentEnd));
                    }
                    currentStart = currentStart.plusDays(1);
                }
                continue;
            }

            while (!occS.isAfter(periodEnd)) {
                if (!occE.isBefore(periodStart) && !occS.isAfter(periodEnd)) {
                    out.add(buildOccurrence(m, occS, occE));
                }
                LocalDateTime nextS = rt.next(occS);
                LocalDateTime nextE = rt.next(occE);

                if (nextS.isEqual(occS) || nextS.isBefore(occS)) {
                    break;
                }

                occS = nextS;
                occE = nextE;
            }
        }
        return out;
    }

    @Override
    public void validateRepeatPeriod(LocalDateTime start, LocalDateTime end,
            RepeatType repeatType) {
        if (repeatType == null) {
            return; // 반복주기 없음은 제한 없음
        }

        LocalDateTime maxEnd;
        switch (repeatType) {
            case DAY:
                maxEnd = start.plusMonths(3);    // 3개월
                break;
            case WEEK:
                maxEnd = start.plusMonths(6);    // 6개월
                break;
            case MONTH:
                maxEnd = start.plusMonths(36);   // 36개월
                break;
            case YEAR:
                maxEnd = start.plusYears(10);    // 10년
                break;
            default:
                return;
        }

        if (end.isAfter(maxEnd)) {
            throw new RepeatPeriodException(
                    String.format("선택하신 반복주기(%s)에서는 종료일이 시작일로부터 %s까지만 가능합니다.",
                            repeatType.getCode(), maxEnd.toLocalDate())
            );
        }
    }

    public void validateDateOrder(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("시작일, 종료일은 필수입니다.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }
    }

}
