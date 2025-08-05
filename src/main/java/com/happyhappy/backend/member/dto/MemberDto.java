package com.happyhappy.backend.member.dto;

import com.happyhappy.backend.member.domain.Member;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Getter
    @Builder
    public static class LoginResponse {

        private String accessToken;
        private String refreshToken;
        private MemberInfoResponse memberInfo;

        public static LoginResponse fromEntity(String accessToken,
                String refreshToken,
                MemberInfoResponse memberInfo) {
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .memberInfo(memberInfo)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        private String userid;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;
    }

    @Getter
    @Builder
    public static class MemberInfoResponse {

        private UUID memberId;
        private String username;
        private String nickname;
        private String imageUrl; // 프로필 사진 지정여부 확인하기
        private LocalDateTime marketingAgreedAt;

        public static MemberInfoResponse fromEntity(Member member) {
            return MemberInfoResponse.builder()
                    .memberId(member.getMemberId())
                    .username(member.getUsername())
                    .nickname(member.getNickname())
                    .imageUrl(member.getImageUrl())
                    .marketingAgreedAt(member.getMarketingAgreedAt())
                    .build();
        }
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignupRequest {

        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Pattern(
                regexp = "^[가-힣]{1,10}$|^[a-zA-Z]{1,20}$",
                message = "이름은 한글(10자 이하) 또는 영문(20자 이하)만 입력 가능하며, 혼용은 불가능합니다."
        )
        private String nickname;

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Pattern(
                regexp = "^[a-zA-Z@._\\-]{6,}$",
                message = "아이디는 영문과 특수기호(@ . - _)만 사용 가능하며, 6자 이상이어야 합니다."
        )
        private String userid;

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message = "비밀번호는 영문, 숫자, 특수문자 중 3개 이상을 포함하여 8자 이상이어야 합니다."
        )
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
        private String passwordCheck;

        @AssertTrue(message = "개인정보 수집 동의는 필수입니다.")
        private boolean privacyAgreement;

        @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
        public boolean isPasswordConfirmed() {
            return password != null && password.equals(passwordCheck);
        }
    }


    @Getter
    @Builder
    public static class SignupResponse {

        private UUID memberId;
        private String userid;

        public static SignupResponse fromEntity(Member member) {
            return SignupResponse.builder()
                    .memberId(member.getMemberId())
                    .userid(member.getUserId())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    public static class CheckResponse {

        private boolean available;
        private String message;
        private CheckType type;

        public enum CheckType {
            USERID, USERNAME
        }

        public static CheckResponse of(CheckType type, boolean isDuplicate) {
            return new CheckResponse(
                    !isDuplicate,
                    isDuplicate ? "이미 사용 중인 " + type.name().toLowerCase() + "입니다."
                            : "사용 가능한 " + type.name().toLowerCase() + "입니다.",
                    type
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UseridCheckRequest {

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Pattern(
                regexp = "^[a-zA-Z@._\\-]{6,}$",
                message = "아이디는 영문과 특수기호(@ . - _)만 사용 가능하며, 6자 이상이어야 합니다."
        )
        private String userid;
    }

}


