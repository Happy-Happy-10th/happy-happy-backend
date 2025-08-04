package com.happyhappy.backend.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EmailDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsernameRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String username;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsernameCodeRequest {
        private String username;
        private String code;
    }

    @Getter
    @AllArgsConstructor
    public static class UsernameResponse {
        private boolean success;
        private String message;

        public static UsernameResponse of(boolean success, String message) {
            return new UsernameResponse(success, message);
        }
    }

}
