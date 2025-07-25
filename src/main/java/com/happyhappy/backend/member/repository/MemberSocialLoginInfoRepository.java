package com.happyhappy.backend.member.repository;

import com.happyhappy.backend.member.domain.MemberSocialLoginInfo;
import com.happyhappy.backend.member.enums.SocialLoginProviderType;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSocialLoginInfoRepository extends
        JpaRepository<MemberSocialLoginInfo, UUID> {

    Optional<MemberSocialLoginInfo> findByProviderTypeAndProviderId(
            @NotNull SocialLoginProviderType providerType, @NotNull String providerId);

    Optional<MemberSocialLoginInfo> findByMemberId(UUID memberId);
}
