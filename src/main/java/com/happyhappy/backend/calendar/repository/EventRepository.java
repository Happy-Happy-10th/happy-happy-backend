package com.happyhappy.backend.calendar.repository;

import com.happyhappy.backend.calendar.domain.Event;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCalendar_CalendarIdAndStartDateBefore(
            Long calendarId,
            LocalDateTime periodEnd
    );
}
