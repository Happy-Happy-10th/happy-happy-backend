package com.happyhappy.backend.authentication.dto;

import lombok.Builder;
import lombok.Getter;

public class TokenDto {

    @Getter
    @Builder
    public static class JwtDto {

        private String accessToken;
        private String refreshToken;
    }
}
