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
    public static class EmailRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailCodeRequest {
        private String email;
        private String code;
    }

    @Getter
    @AllArgsConstructor
    public static class EmailResponse {
        private boolean success;
        private String message;

        public static EmailResponse of(boolean success, String message) {
            return new EmailResponse(success, message);
        }
    }

}
