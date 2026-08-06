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
            Boolean onboardingCompleted,
            Boolean termsAgreed
    ) {
        return MemberResDTO.Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .onboardingCompleted(onboardingCompleted)
                .termsAgreed(termsAgreed)
                .build();
    }

    public static MemberResDTO.MyInfo toMyInfo(Member member, String characterImageUrl) {


        return MemberResDTO.MyInfo.builder()
                .memberId(member.getId())
                .name(member.getName())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .email(member.getEmail())
                .provider(member.getProvider())
                .onboardingCompleted(member.getOnboardingCompleted())
                .createdAt(member.getCreatedAt())
                .characterImageUrl(characterImageUrl)
                .build();
    }

    public static MemberResDTO.TokenReissue toTokenReissueResponse(
            String accessToken,
            String refreshToken,
            Boolean onboardingCompleted,
            Boolean termsAgreed
    ) {
        return MemberResDTO.TokenReissue.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .onboardingCompleted(onboardingCompleted)
                .termsAgreed(termsAgreed)
                .build();
    }
}
