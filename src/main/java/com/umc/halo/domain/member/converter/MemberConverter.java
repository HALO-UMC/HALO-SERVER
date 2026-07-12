package com.umc.halo.domain.member.converter;

import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;

public class MemberConverter {

    public static Member toMember(Provider provider, String providerId) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    public static MemberResDTO.LoginResponse toLoginResponse(
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {
        return MemberResDTO.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .onboardingCompleted(onboardingCompleted)
                .build();
    }
}
