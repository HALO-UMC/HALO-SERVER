package com.umc.halo.domain.member.converter;

import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;

public class MemberConverter {

    public static Member toMember(Provider provider, OidcUserInfo oidcUserInfo) {
        return Member.builder()
                .provider(provider)
                .providerId(oidcUserInfo.providerId())
                .email(oidcUserInfo.email())
                .build();
    }

    public static MemberResDTO.Login toLoginResponse(
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {
        return MemberResDTO.Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .onboardingCompleted(onboardingCompleted)
                .build();
    }

    public static MemberResDTO.MyInfo toMyInfo(Member member) {
        return MemberResDTO.MyInfo.builder()
                .memberId(member.getId())
                .name(member.getName())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .provider(member.getProvider())
                .onboardingCompleted(member.getOnboardingCompleted())
                .createdAt(member.getCreatedAt())
                .build();
    }

    public static MemberResDTO.TokenReissue toTokenReissueResponse(
            String accessToken,
            String refreshToken
    ) {
        return MemberResDTO.TokenReissue.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
