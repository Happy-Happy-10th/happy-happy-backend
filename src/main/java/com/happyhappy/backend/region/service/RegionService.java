package com.happyhappy.backend.region.service;


import com.happyhappy.backend.calendar.domain.CalendarDto.SidoInfo;
import com.happyhappy.backend.calendar.domain.CalendarDto.SigunguInfo;
import com.happyhappy.backend.region.domain.Region;
import com.happyhappy.backend.region.repository.RegionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;

    public List<SidoInfo> getAllSidoList() {
        return regionRepository.findDistinctSidoAsDto();
    }

    public List<SigunguInfo> getSigunguListBySido(String sidoCode) {
        return regionRepository.findSigunguBySidoCodeAsDto(sidoCode);
    }

    public Optional<Region> findRegionByCode(String sidoCode, String sigunguCode) {
        return regionRepository.findBySidoCodeAndSigunguCode(sidoCode, sigunguCode);
    }
}
