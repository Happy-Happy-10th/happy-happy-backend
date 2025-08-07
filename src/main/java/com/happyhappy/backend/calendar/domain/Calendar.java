package com.happyhappy.backend.calendar.domain;

import com.happyhappy.backend.calendar.enums.AiTone;
import com.happyhappy.backend.calendar.enums.ColorBlindMode;
import com.happyhappy.backend.calendar.enums.TimeFormat;
import com.happyhappy.backend.calendar.enums.WeekStartDay;
import com.happyhappy.backend.calendar.enums.WeekendType;
import com.happyhappy.backend.common.domain.BaseEntity;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.region.domain.Region;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CALENDAR")
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("캘린더 고유 식별자")
    @Column(name = "CALENDAR_ID")
    private Long calendarId;

    @OneToOne(mappedBy = "calendar")
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "WEEK_START_DAY")
    @Comment("캘린더 시작요일 설정")
    @Builder.Default
    private WeekStartDay weekStartDay = WeekStartDay.MONDAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "COLOR_BLIND_MODE")
    @Comment("색맹 색상 설정")
    @Builder.Default
    private ColorBlindMode colorBlindMode = ColorBlindMode.NOMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIME_FORMAT")
    @Comment("시간 형식 설정")
    @Builder.Default
    private TimeFormat timeFormat = TimeFormat.TWELVE_HOUR;

    @Enumerated(EnumType.STRING)
    @Column(name = "WEEKEND_TYPE")
    @Comment("휴일 설정 타입")
    @Builder.Default
    private WeekendType weekendType = WeekendType.DEFAULT;

    @Column(name = "WEEKEND_DAYS", length = 500)
    @Comment("사용자 선택 휴일")
    @Convert(converter = DayOfWeekListConverter.class)
    private List<DayOfWeek> weekendDays = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "AI_TONE")
    @Builder.Default
    private AiTone aiTone = AiTone.PLAIN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AI_REGIONG_ID")
    @Comment("AI 검색 지역 (NULL 허용 - 미선택시)")
    private Region aiSearchRegion;

    public List<DayOfWeek> getEffectiveWeekendDays() {

        if (weekendType == WeekendType.CUSTOM) {
            return weekendDays != null ? new ArrayList<>(weekendDays) : new ArrayList<>();
        }
        return Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    }

    public void updateWeekStartDay(WeekStartDay weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public void updateColorBlindMode(ColorBlindMode colorBlindMode) {
        this.colorBlindMode = colorBlindMode;
    }

    public void updateTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void updateWeekendSettings(WeekendType weekendType, List<DayOfWeek> weekendDays) {
        this.weekendType = weekendType;
        if (weekendType == WeekendType.DEFAULT) {
            this.weekendDays = new ArrayList<>();
        } else {
            this.weekendDays =
                    weekendDays != null ? new ArrayList<>(weekendDays) : new ArrayList<>();
        }
    }

    public void updateAiTone(AiTone aiTone) {
        this.aiTone = aiTone;
    }

    // 지역 검색 관련

    public String getAiRegionSidoCode() {
        return aiSearchRegion != null ? aiSearchRegion.getSidoCode() : null;
    }

    public String getAiRegionSidoName() {
        return aiSearchRegion != null ? aiSearchRegion.getSidoName() : null;
    }

    public String getAiRegionSigunguCode() {
        return aiSearchRegion != null ? aiSearchRegion.getSigunguCode() : null;
    }

    public String getAiRegionSigunguName() {
        return aiSearchRegion != null ? aiSearchRegion.getSigunguName() : null;
    }

    // 지역 선택 여부 확인
    public boolean hasAiRegion() {
        return aiSearchRegion != null;
    }

    // AI 지역 설정 업데이트 (NULL 허용)
    public void updateAiRegion(Region region) {
        this.aiSearchRegion = region; // NULL도 가능
    }

    // AI 지역 제거
    public void clearAiRegion() {
        this.aiSearchRegion = null;
    }
}
