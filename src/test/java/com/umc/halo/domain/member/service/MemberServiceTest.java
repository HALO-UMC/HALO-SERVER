package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.domain.member.oauth.AbstractOidcProvider;
import com.umc.halo.domain.member.oauth.OidcProviderFactory;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * MemberService.login은 @Transactional이 없어야 함
 * OIDC 검증(외부 JWKS 호출)이 DB 쓰기보다 먼저 일어나는지 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private OidcProviderFactory oidcProviderFactory;
    @Mock
    private AbstractOidcProvider oidcProvider;
    @Mock
    private MemberWriter memberWriter;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 로그인시_OIDC_검증을_먼저_수행한_뒤_MemberWriter로_위임한다() {
        MemberReqDTO.Login dto = new MemberReqDTO.Login("KAKAO", "provider-token");
        OidcUserInfo oidcUserInfo = new OidcUserInfo("provider-id-1", "user@test.com");
        MemberResDTO.Login expected = MemberResDTO.Login.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .isNewUser(false)
                .onboardingCompleted(true)
                .termsAgreed(true)
                .build();

        given(oidcProviderFactory.getProvider(Provider.KAKAO)).willReturn(oidcProvider);
        given(oidcProvider.verify("provider-token")).willReturn(oidcUserInfo);
        given(memberWriter.persist(Provider.KAKAO, oidcUserInfo)).willReturn(expected);

        MemberResDTO.Login result = memberService.login(dto);

        assertThat(result).isEqualTo(expected);

        InOrder inOrder = inOrder(oidcProviderFactory, oidcProvider, memberWriter);
        inOrder.verify(oidcProviderFactory).getProvider(Provider.KAKAO);
        inOrder.verify(oidcProvider).verify("provider-token");
        inOrder.verify(memberWriter).persist(Provider.KAKAO, oidcUserInfo);
    }

    @Test
    void 지원하지_않는_provider면_OIDC_검증_없이_바로_예외를_던진다() {
        MemberReqDTO.Login dto = new MemberReqDTO.Login("APPLE", "provider-token");

        assertThatThrownBy(() -> memberService.login(dto))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(AuthErrorCode.UNSUPPORTED_PROVIDER));

        verifyNoInteractions(oidcProviderFactory, oidcProvider, memberWriter);
    }
}