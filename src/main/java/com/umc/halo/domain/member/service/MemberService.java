package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.domain.member.oauth.AbstractOidcProvider;
import com.umc.halo.domain.member.oauth.OidcProviderFactory;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import com.umc.halo.global.security.HashUtil;
import com.umc.halo.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final HashUtil hashUtil;
    private final OidcProviderFactory oidcProviderFactory;

    @Transactional
    public MemberResDTO.Login login(MemberReqDTO.Login dto) {

        Provider provider;

        try {
            provider = Provider.valueOf(dto.provider());
        } catch (IllegalArgumentException e) {
            throw new ProjectException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        AbstractOidcProvider oidcProvider = oidcProviderFactory.getProvider(provider);
        String providerId = oidcProvider.verify(dto.providerToken());

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId).orElse(null);
        boolean isNewUser = false;

        if (member == null) {
            try{
                member = MemberConverter.toMember(provider, providerId);
                memberRepository.save(member);
                isNewUser = true;
            } catch (DataIntegrityViolationException e) {
                member = memberRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(() -> e);
            }

        }

        String accessToken = jwtUtil.createAccessToken(member.getId());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        member.updateRefreshTokenToHash(hashUtil.hash(refreshToken));

        return MemberConverter.toLoginResponse(accessToken, refreshToken, isNewUser, member.getOnboardingCompleted());
    }
}
