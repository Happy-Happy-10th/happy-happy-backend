package com.happyhappy.backend.region.repository;

import com.happyhappy.backend.calendar.domain.CalendarDto.SidoInfo;
import com.happyhappy.backend.calendar.domain.CalendarDto.SigunguInfo;
import com.happyhappy.backend.region.domain.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    /***
     * 시도 목록 조회
     */
    @Query("SELECT new com.happyhappy.backend.calendar.domain.CalendarDto$SidoInfo(r.sidoCode, r.sidoName)"
            + " FROM Region r "
            + "GROUP BY r.sidoCode, r.sidoName "
            + "ORDER BY r.sidoCode")
    List<SidoInfo> findDistinctSidoAsDto();

    /***
     * 시군구 목록 조회
     * @param sidoCode
     */
    @Query("SELECT new com.happyhappy.backend.calendar.domain.CalendarDto$SigunguInfo(r.sigunguCode, r.sigunguName) "
            + "FROM Region r "
            + "WHERE r.sidoCode = :sidoCode ORDER BY r.sigunguCode")
    List<SigunguInfo> findSigunguBySidoCodeAsDto(@Param("sidoCode") String sidoCode);

    /***
     *
     * PATCH용
     ***/
    @Query("SELECT r FROM Region r WHERE r.sidoCode = :sidoCode AND r.sigunguCode = :sigunguCode")
    Optional<Region> findBySidoCodeAndSigunguCode(
            @Param("sidoCode") String sidoCode,
            @Param("sigunguCode") String sigunguCode);
}
