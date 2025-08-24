package com.happyhappy.backend.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FindUserIdRequest {

        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String nickname;
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String username;
    }

    @Getter
    @AllArgsConstructor
    public static class FindUserIdResponse {

        private boolean success;
        private String message;
        private String userId;

        public static FindUserIdResponse of(boolean success, String message, String userId) {
            return new FindUserIdResponse(success, message, userId);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FindPasswordRequest {

        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String nickname;
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        private String userid;
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String username;
    }

    @Getter
    @AllArgsConstructor
    public static class FindPasswordResponse {

        private boolean success;
        private String message;
        private String password;

        public static FindPasswordResponse of(boolean success, String message, String password) {
            return new FindPasswordResponse(success, message, password);
        }
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResetPasswordRequest {

        private String username;
        private String userid;
        @NotBlank(message = "새 비밀번호를 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message = "비밀번호는 영문, 숫자, 특수문자 중 3개 이상을 포함하여 8자 이상이어야 합니다."
        )
        private String newPassword;
        @NotBlank(message = "새 비밀번호 확인을 입력하세요.")
        private String passwordCheck;

        @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
        public boolean isPasswordConfirmed() {
            return newPassword != null && newPassword.equals(passwordCheck);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ResetPasswordResponse {

        private boolean success;
        private String message;

        public static ResetPasswordResponse of(boolean success, String message) {
            return new ResetPasswordResponse(success, message);
        }
    }

}
