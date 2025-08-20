package com.happyhappy.backend.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 604800) // 7일 (7 * 24 * 60 * 60)
@RequiredArgsConstructor
public class RedisConfig {

}
