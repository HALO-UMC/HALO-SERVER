package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.setting.converter.SettingConverter;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import com.umc.halo.global.security.JwtUtil;
import com.umc.halo.global.util.HashUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * MemberWriter는 findByProviderAndProviderIdForUpdate로 잠근 member를 같은 트랜잭션 안에서 바로 수정/저장
 * 락+쓰기가 실제로 한 덩어리로 동작하는지를 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberWriterTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberSettingRepository memberSettingRepository;
    @Mock
    private BgmRepository bgmRepository;
    @Mock
    private MemberTermRepository memberTermRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HashUtil hashUtil;

    @InjectMocks
    private MemberWriter memberWriter;

    private final Provider provider = Provider.KAKAO;
    private final OidcUserInfo oidcUserInfo = new OidcUserInfo("provider-id-1", "user@test.com");

    @Test
    void 신규_회원이면_생성하고_기본_BGM_설정을_저장한다() {
        given(memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.empty());
        given(bgmRepository.findById(1L))
                .willReturn(Optional.of(Bgm.builder().id(1L).title("기본 BGM").build()));
        given(jwtUtil.createAccessToken(any())).willReturn("access-token");
        given(jwtUtil.createRefreshToken(any())).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(any())).willReturn(false);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        assertThat(response.isNewUser()).isTrue();
        verify(memberRepository).save(any(Member.class));
        verify(memberSettingRepository).save(any());
    }

    @Test
    void 기존_회원이면_새로_만들지_않고_refreshToken만_갱신한다() {
        Member existing = Member.builder()
                .id(10L)
                .provider(provider)
                .providerId(oidcUserInfo.providerId())
                .build();
        given(memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.of(existing));
        given(jwtUtil.createAccessToken(10L)).willReturn("access-token");
        given(jwtUtil.createRefreshToken(10L)).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(10L)).willReturn(true);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        assertThat(response.isNewUser()).isFalse();
        assertThat(existing.getRefreshTokenHash()).isEqualTo("hashed-refresh-token");
        verify(memberRepository, never()).save(any());
    }

    @Test
    void 동시_로그인으로_저장이_충돌하면_다시_조회해서_기존_회원을_사용한다() {
        Member raceWinner = Member.builder()
                .id(20L)
                .provider(provider)
                .providerId(oidcUserInfo.providerId())
                .build();
        given(memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.empty(), Optional.of(raceWinner));
        doThrow(new DataIntegrityViolationException("duplicate")).when(memberRepository).save(any(Member.class));
        given(jwtUtil.createAccessToken(20L)).willReturn("access-token");
        given(jwtUtil.createRefreshToken(20L)).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(20L)).willReturn(false);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        // save()가 memberSetting 저장 전에 실패했으므로 isNewUser는 true로 세팅되지 않음
        assertThat(response.isNewUser()).isFalse();
        assertThat(raceWinner.getRefreshTokenHash()).isEqualTo("hashed-refresh-token");
    }
}