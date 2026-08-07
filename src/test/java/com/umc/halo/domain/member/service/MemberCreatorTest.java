package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * REQUIRES_NEW로 격리된 회원 생성 로직 자체를 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberCreatorTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberSettingRepository memberSettingRepository;
    @Mock
    private BgmRepository bgmRepository;

    @InjectMocks
    private MemberCreator memberCreator;

    private final Provider provider = Provider.KAKAO;
    private final OidcUserInfo oidcUserInfo = new OidcUserInfo("provider-id-1", "user@test.com");

    @Test
    void 회원과_기본_BGM_설정을_생성해서_저장한다() {
        given(bgmRepository.findById(1L))
                .willReturn(Optional.of(Bgm.builder().id(1L).title("기본 BGM").build()));

        Member created = memberCreator.create(provider, oidcUserInfo);

        assertThat(created.getProvider()).isEqualTo(provider);
        assertThat(created.getProviderId()).isEqualTo(oidcUserInfo.providerId());
        verify(memberRepository).save(created);
        verify(memberSettingRepository).save(any());
    }
}