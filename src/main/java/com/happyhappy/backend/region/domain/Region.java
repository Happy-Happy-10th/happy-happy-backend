package com.happyhappy.backend.region.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REGION",
        indexes = {
                @Index(name = "IDX_REGION_SIDO", columnList = "SIDO_CODE"),
                @Index(name = "IDX_REGION_SIGUNGU", columnList = "SIDO_CODE, SIGUNGU_CODE")
        })
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("지역 고유 ID")
    private Long id;

    @Column(name = "SIDO_CODE", length = 2, nullable = false)
    @Comment("시도 코드")
    private String sidoCode;

    @Column(name = "SIDO_NAME", length = 50, nullable = false)
    @Comment("시도명")
    private String sidoName;

    @Column(name = "SIGUNGU_CODE", length = 10, nullable = false)
    @Comment("시군구 코드")
    private String sigunguCode;

    @Column(name = "SIGUNGU_NAME", length = 100, nullable = false)
    @Comment("시군구명")
    private String sigunguName;


}
