package com.happyhappy.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO 수정 예정
//@OpenAPIDefinition(
//        servers = {
//                @Server(url = "https://yottaeyo.site", description = "운영"),
//                @Server(url = "http://localhost:8080", description = "로컬")
//        }
//)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Yottaeyo API Swagger").version("1.0"))
                .addSecurityItem(
                        new SecurityRequirement().addList("Authorization")) // 모든 API 요청에 토큰 자동 포함
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer") // (Authorization: Bearer 토큰)
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                        ));
    }
}
