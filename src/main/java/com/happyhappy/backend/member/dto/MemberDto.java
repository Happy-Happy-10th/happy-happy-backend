package com.happyhappy.backend.member.dto;

import com.happyhappy.backend.member.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String username;

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
}
