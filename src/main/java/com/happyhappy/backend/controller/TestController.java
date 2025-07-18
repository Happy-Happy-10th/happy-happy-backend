package com.happyhappy.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    @GetMapping("/health")
    public String apiTest() {
        System.out.println("yottaeyo-health-check!");
        return "yottaeyo-health check!";
    }
}
