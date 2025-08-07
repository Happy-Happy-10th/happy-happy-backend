package com.happyhappy.backend.calendar.domain;

import com.happyhappy.backend.calendar.enums.EventColor;
import com.happyhappy.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EVENT")
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    @Comment("이벤트 고유 식별자")
    private Long eventId;

    @Column(name = "TITLE", nullable = false)
    @Comment("이벤트 제목")
    private String title;

    @Column(name = "ALL_DAY", nullable = false)
    @Comment("하루 종일 여부")
    private boolean allDay;

    @Column(name = "START_DATE", nullable = false)
    @Comment("이벤트 시작 날짜/시간")
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    @Comment("이벤트 종료 날짜/시간")
    private LocalDateTime endDate;

    @Column(name = "REPEAT_CYCLE")
    @Comment("반복 주기 (null, day , week, month, year)")
    private String repeatCycle;

    @Enumerated(EnumType.STRING)
    @Column(name = "COLOR")
    private EventColor color;

    @Column(name = "LOCATE")
    @Comment("이벤트 위치")
    private String locate;

    @Column(name = "MEMO", columnDefinition = "TEXT")
    @Comment("이벤트 메모")
    private String memo;

    @Column(name = "IS_HOLIDAY", nullable = false)
    @Comment("공휴일 여부")
    private boolean isHoliday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID", nullable = false)
    @Comment("참조하는 캘린더 ID")
    private Calendar calendar;
}
