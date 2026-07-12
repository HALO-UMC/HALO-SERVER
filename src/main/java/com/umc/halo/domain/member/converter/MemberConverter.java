package com.umc.halo.domain.member.converter;

import com.umc.halo.domain.member.dto.response.LoginResponseDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;

public class MemberConverter {

    public static Member toMember(Provider provider, String providerId) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    public static LoginResponseDTO.LoginResponse toLoginResponse(
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {
        return LoginResponseDTO.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .onboardingCompleted(onboardingCompleted)
                .build();
    }
}
