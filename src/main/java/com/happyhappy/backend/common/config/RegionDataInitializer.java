package com.happyhappy.backend.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.happyhappy.backend.region.domain.Region;
import com.happyhappy.backend.region.repository.RegionRepository;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegionDataInitializer {

    private final RegionRepository regionRepository;

    @Bean
    CommandLineRunner initRegionData() {
        return args -> {
            if (regionRepository.count() > 0) {
                log.info("Region Data Already Exists");
                return;
            }
            loadRegionData();
        };
    }

    @Transactional
    public void loadRegionData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            ClassPathResource resource = new ClassPathResource("data/address_List.json");

            InputStream inputStream = resource.getInputStream();

            List<RegionDto> regionDtos = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<RegionDto>>() {
                    });

            Set<String> uniqueSidoCodes = new HashSet<>();

            for (RegionDto regionDto : regionDtos) {
                String key = regionDto.getSidoCode() + "-" + regionDto.getSigunguCode();

                if (uniqueSidoCodes.contains(key)) {
                    log.warn("Duplicate Sido Code: {}", key);
                    continue;
                }
                Region region = Region.builder()
                        .sidoCode(regionDto.getSidoCode())
                        .sidoName(regionDto.getSidoName())
                        .sigunguCode(regionDto.getSigunguCode())
                        .sigunguName(regionDto.getSigunguName())
                        .build();

                regionRepository.save(region);
                uniqueSidoCodes.add(key);
            }
            log.info("Successfully loaded {} region records", regionRepository.count());
        } catch (Exception e) {
            log.error("Failed to load region data", e);
            throw new RuntimeException("Failed to load region data", e);
        }
    }

    @Data
    private static class RegionDto {

        private String sidoCode;
        private String sidoName;
        private String sigunguCode;
        private String sigunguName;
    }

}
