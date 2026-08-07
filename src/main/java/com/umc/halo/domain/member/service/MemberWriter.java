package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import com.umc.halo.global.security.JwtUtil;
import com.umc.halo.global.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberWriter {

    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final JwtUtil jwtUtil;
    private final HashUtil hashUtil;
    private final MemberCreator memberCreator;

    @Transactional
    public MemberResDTO.Login persist(Provider provider, OidcUserInfo oidcUserInfo) {

        Member member = memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId()).orElse(null);
        boolean isNewUser = false;

        if (member == null) {
            try {
                member = memberCreator.create(provider, oidcUserInfo);
                isNewUser = true;
            } catch (DataIntegrityViolationException e) {
                member = memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()).orElseThrow(() -> e);
            }
        }

        String accessToken = jwtUtil.createAccessToken(member.getId());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        member.updateRefreshTokenToHash(hashUtil.hash(refreshToken));

        boolean termsAgreed = memberTermRepository.areAllRequiredTermsAgreed(member.getId());

        return MemberConverter.toLoginResponse(accessToken, refreshToken, isNewUser, member.getOnboardingCompleted(), termsAgreed);
    }
}