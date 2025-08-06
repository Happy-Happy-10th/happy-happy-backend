package com.happyhappy.backend.region.controller;

import com.happyhappy.backend.calendar.domain.CalendarDto.SidoInfo;
import com.happyhappy.backend.calendar.domain.CalendarDto.SigunguInfo;
import com.happyhappy.backend.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
@Tag(name = "region", description = "지역 시군구 관련 api")
public class RegionController {

    private final RegionService regionService;

    @Operation(summary = "시도 목록 조회", description = "전체 시도 목록을 조회합니다.")
    @GetMapping("/sido")
    public ResponseEntity<List<SidoInfo>> getSidoList() {
        List<SidoInfo> sidoList = regionService.getAllSidoList();
        return ResponseEntity.ok(sidoList);
    }

    @Operation(summary = "시군구 목록 조회", description = "특정 시군구 목록을 조회합니다.")
    @GetMapping("/sigungu")
    public ResponseEntity<List<SigunguInfo>> getSigunguListBySido(@RequestParam String sidoCode) {
        List<SigunguInfo> sigunguList = regionService.getSigunguListBySido(sidoCode);
        return ResponseEntity.ok(sigunguList);
    }
}
