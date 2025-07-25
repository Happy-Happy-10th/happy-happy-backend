package com.happyhappy.backend.member.domain;

import com.happyhappy.backend.member.enums.SocialLoginProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_SOCIAL_LOGIN_INFO")
public class MemberSocialLoginInfo {

    @Id
    @Column(name = "MEMBER_ID", columnDefinition = "BINARY(16)")
    @Comment("회원 ID")
    private UUID memberId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER_TYPE")
    @Comment("소셜 로그인 서비스 제공자")
    private SocialLoginProviderType providerType;

    @NotNull
    @Column(name = "PROVIDER_ID")
    @Comment("소셜 로그인 서비스 제공자 ID")
    private String providerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOCIAL_LOGIN_MEMBER"))
    private Member member;
}
