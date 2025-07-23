package com.happyhappy.backend.test;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "토큰 인증이 필요한 테스트 API입니다.")
@RestController
public class TestController {
    @Operation(summary = "Test API")
    @GetMapping("/health")
    public String apiTest() {
        System.out.println("yottaeyo-health-check!");
        return "yottaeyo-health check!";
    }
}