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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
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
    @NotNull
    private String title;

    @Column(name = "ALL_DAY")
    @Comment("하루 종일 여부")
    private boolean allDay;

    @Column(name = "START_DATE", nullable = false)
    @Comment("이벤트 시작 날짜/시간")
    @NotNull
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    @Comment("이벤트 종료 날짜/시간")
    @NotNull
    private LocalDateTime endDate;

    @Column(name = "REPEAT_CYCLE")
    @Comment("반복 주기 (null, day , week, month, year)")
    @Builder.Default
    private String repeatCycle = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "COLOR")
    private EventColor color;

    @Column(name = "LOCATE")
    @Comment("이벤트 위치")
    private String locate;

    @Column(name = "MEMO", columnDefinition = "TEXT")
    @Comment("이벤트 메모")
    private String memo;

    @Column(name = "IS_HOLIDAY")
    @Comment("공휴일 여부")
    @Builder.Default
    private boolean isHoliday = false;

    @Column(name = "IS_PENDING")
    @Comment("보류 여부")
    @Builder.Default
    private boolean isPending = false;

    @Column(name = "IS_YOTEYO")
    @Comment("AI 여부")
    @Builder.Default
    private boolean isYoteyo = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "CALENDAR_ID")
    @Comment("참조하는 캘린더 ID")
    private Calendar calendar;

    @Column(name = "HOMEPAGE_URL")
    @Comment("홈페이지 URL")
    private String homepageUrl;

    @Column(name = "DETAILPAGE_URL")
    @Comment("상세 페이지 URL")
    private String detailPageUrl;

    @Column(name = "CONFIDENCE")
    @Comment("신뢰도")
    private Integer confidence;
}
