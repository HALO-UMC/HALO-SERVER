package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.request.LoginRequestDTO;
import com.umc.halo.domain.member.dto.response.LoginResponseDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.oauth.OidcProvider;
import com.umc.halo.domain.member.oauth.OidcProviderFactory;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.global.security.HashUtil;
import com.umc.halo.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
    public LoginResponseDTO.LoginResponse login(LoginRequestDTO.Login dto) {

        OidcProvider oidcProvider = oidcProviderFactory.getProvider(dto.provider());
        String providerId = oidcProvider.verify(dto.providerToken());

        Member member = memberRepository.findByProviderAndProviderId(dto.provider(), providerId).orElse(null);
        boolean isNewUser = false;

        if (member == null) {
            member = MemberConverter.toMember(dto.provider(), providerId);
            memberRepository.save(member);
            isNewUser = true;
        }

        String accessToken = jwtUtil.createAccessToken(member.getId());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        member.updateRefreshTokenToHash(hashUtil.hash(refreshToken));

        return MemberConverter.toLoginResponse(accessToken, refreshToken, isNewUser, member.getOnboardingCompleted());
    }
}
