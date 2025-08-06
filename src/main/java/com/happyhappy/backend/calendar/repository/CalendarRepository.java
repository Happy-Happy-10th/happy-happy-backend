package com.happyhappy.backend.calendar.repository;

import com.happyhappy.backend.calendar.domain.Calendar;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Optional<Calendar> findByCalendarId(Long calendarId);

}
