package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.setting.converter.SettingConverter;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
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
    private final MemberSettingRepository memberSettingRepository;
    private final BgmRepository bgmRepository;
    private final MemberTermRepository memberTermRepository;
    private final JwtUtil jwtUtil;
    private final HashUtil hashUtil;

    @Transactional
    public MemberResDTO.Login persist(Provider provider, OidcUserInfo oidcUserInfo) {

        Member member = memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()).orElse(null);
        boolean isNewUser = false;

        if (member == null) {
            try {
                member = MemberConverter.toMember(provider, oidcUserInfo);
                memberRepository.save(member);

                Bgm defaultBgm = bgmRepository.findById(1L).orElseThrow(() -> new SettingException(SettingErrorCode.BGM_NOT_FOUND));

                MemberSetting memberSetting = SettingConverter.toMemberSetting(member, defaultBgm);
                memberSettingRepository.save(memberSetting);

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
