package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
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
 * MemberWriter는 첫 조회는 락 없이, 생성 충돌 후 재조회는 FOR UPDATE로
 * 신규 생성은 MemberCreator(REQUIRES_NEW)에 위임하고,
 * 실패 시 이 트랜잭션은 오염되지 않은 채로 재조회를 이어가는지를 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberWriterTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberTermRepository memberTermRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HashUtil hashUtil;
    @Mock
    private MemberCreator memberCreator;

    @InjectMocks
    private MemberWriter memberWriter;

    private final Provider provider = Provider.KAKAO;
    private final OidcUserInfo oidcUserInfo = new OidcUserInfo("provider-id-1", "user@test.com");

    @Test
    void 신규_회원이면_MemberCreator에게_생성을_위임한다() {
        Member created = Member.builder().id(99L).provider(provider).providerId(oidcUserInfo.providerId()).build();
        given(memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.empty());
        given(memberCreator.create(provider, oidcUserInfo)).willReturn(created);
        given(jwtUtil.createAccessToken(99L)).willReturn("access-token");
        given(jwtUtil.createRefreshToken(99L)).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(99L)).willReturn(false);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        assertThat(response.isNewUser()).isTrue();
        assertThat(created.getRefreshTokenHash()).isEqualTo("hashed-refresh-token");
        verify(memberCreator).create(provider, oidcUserInfo);
    }

    @Test
    void 기존_회원이면_새로_만들지_않고_refreshToken만_갱신한다() {
        Member existing = Member.builder()
                .id(10L)
                .provider(provider)
                .providerId(oidcUserInfo.providerId())
                .build();
        given(memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.of(existing));
        given(jwtUtil.createAccessToken(10L)).willReturn("access-token");
        given(jwtUtil.createRefreshToken(10L)).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(10L)).willReturn(true);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        assertThat(response.isNewUser()).isFalse();
        assertThat(existing.getRefreshTokenHash()).isEqualTo("hashed-refresh-token");
        verify(memberRepository, never()).save(any());
        verify(memberCreator, never()).create(any(), any());
    }

    @Test
    void 동시_로그인으로_생성이_충돌하면_다시_조회해서_기존_회원을_사용한다() {
        Member raceWinner = Member.builder()
                .id(20L)
                .provider(provider)
                .providerId(oidcUserInfo.providerId())
                .build();
        given(memberRepository.findByProviderAndProviderId(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.empty());
        given(memberRepository.findByProviderAndProviderIdForUpdate(provider, oidcUserInfo.providerId()))
                .willReturn(Optional.of(raceWinner));
        given(memberCreator.create(provider, oidcUserInfo))
                .willThrow(new DataIntegrityViolationException("duplicate"));
        given(jwtUtil.createAccessToken(20L)).willReturn("access-token");
        given(jwtUtil.createRefreshToken(20L)).willReturn("refresh-token");
        given(hashUtil.hash("refresh-token")).willReturn("hashed-refresh-token");
        given(memberTermRepository.areAllRequiredTermsAgreed(20L)).willReturn(false);

        MemberResDTO.Login response = memberWriter.persist(provider, oidcUserInfo);

        assertThat(response.isNewUser()).isFalse();
        assertThat(raceWinner.getRefreshTokenHash()).isEqualTo("hashed-refresh-token");
    }
}