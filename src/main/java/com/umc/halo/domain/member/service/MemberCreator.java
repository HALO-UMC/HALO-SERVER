package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberCreator {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final BgmRepository bgmRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Member create(Provider provider, OidcUserInfo oidcUserInfo) {
        Member member = MemberConverter.toMember(provider, oidcUserInfo);
        memberRepository.save(member);

        Bgm defaultBgm = bgmRepository.findById(1L).orElseThrow(() -> new SettingException(SettingErrorCode.BGM_NOT_FOUND));

        MemberSetting memberSetting = SettingConverter.toMemberSetting(member, defaultBgm);
        memberSettingRepository.save(memberSetting);

        return member;
    }
}