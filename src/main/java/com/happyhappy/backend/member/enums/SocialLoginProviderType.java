package com.happyhappy.backend.member.enums;

import lombok.Getter;

@Getter
public enum SocialLoginProviderType {
    GOOGLE("google"),
    KAKAO("kakao");

    private final String registrationId;

    SocialLoginProviderType(String registrationId) {
        this.registrationId = registrationId;
    }

    public static SocialLoginProviderType fromRegistrationId(String registrationId) {
        for (SocialLoginProviderType type : SocialLoginProviderType.values()) {
            if (type.getRegistrationId().equals(registrationId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown registration id:" + registrationId);
    }
}
