package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.oauth.AbstractOidcProvider;
import com.umc.halo.domain.member.oauth.OidcProviderFactory;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacterVariant;
import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.domain.content.storybook.repository.StorybookCharacterVariantRepository;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import com.umc.halo.global.security.JwtUtil;
import com.umc.halo.global.util.HashUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * MemberService.login은 @Transactional이 없어야 함
 * OIDC 검증(외부 JWKS 호출)이 DB 쓰기보다 먼저 일어나는지 검증
 * tokenReissue/logout/withdraw/getMyInfo의 정상·예외 흐름도 함께 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private OidcProviderFactory oidcProviderFactory;
    @Mock
    private AbstractOidcProvider oidcProvider;
    @Mock
    private MemberWriter memberWriter;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberTermRepository memberTermRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HashUtil hashUtil;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private StorybookCharacterVariantRepository storybookCharacterVariantRepository;

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

    @Test
    void tokenReissue_유효하지_않은_토큰이면_예외() {
        MemberReqDTO.TokenReissue dto = new MemberReqDTO.TokenReissue("bad-token");
        given(jwtUtil.isValid("bad-token")).willReturn(false);

        assertThatThrownBy(() -> memberService.tokenReissue(dto))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN));

        verifyNoInteractions(memberRepository, hashUtil);
    }

    @Test
    void tokenReissue_refreshToken_타입이_아니면_예외() {
        MemberReqDTO.TokenReissue dto = new MemberReqDTO.TokenReissue("access-token-misused");
        given(jwtUtil.isValid("access-token-misused")).willReturn(true);
        given(jwtUtil.isRefreshToken("access-token-misused")).willReturn(false);

        assertThatThrownBy(() -> memberService.tokenReissue(dto))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN));

        verifyNoInteractions(memberRepository, hashUtil);
    }

    @Test
    void tokenReissue_저장된_해시와_불일치하면_예외() {
        MemberReqDTO.TokenReissue dto = new MemberReqDTO.TokenReissue("old-refresh-token");
        Member member = mock(Member.class);

        given(jwtUtil.isValid("old-refresh-token")).willReturn(true);
        given(jwtUtil.isRefreshToken("old-refresh-token")).willReturn(true);
        given(jwtUtil.getMemberId("old-refresh-token")).willReturn(1L);
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.of(member));
        given(member.getRefreshTokenHash()).willReturn("stored-hash");
        given(hashUtil.matches("old-refresh-token", "stored-hash")).willReturn(false);

        assertThatThrownBy(() -> memberService.tokenReissue(dto))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN));
    }

    @Test
    void tokenReissue_정상_흐름이면_새_토큰을_발급하고_해시를_갱신한다() {
        MemberReqDTO.TokenReissue dto = new MemberReqDTO.TokenReissue("old-refresh-token");
        Member member = mock(Member.class);

        given(jwtUtil.isValid("old-refresh-token")).willReturn(true);
        given(jwtUtil.isRefreshToken("old-refresh-token")).willReturn(true);
        given(jwtUtil.getMemberId("old-refresh-token")).willReturn(1L);
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.of(member));
        given(member.getRefreshTokenHash()).willReturn("stored-hash");
        given(hashUtil.matches("old-refresh-token", "stored-hash")).willReturn(true);
        given(jwtUtil.createAccessToken(1L)).willReturn("new-access-token");
        given(jwtUtil.createRefreshToken(1L)).willReturn("new-refresh-token");
        given(hashUtil.hash("new-refresh-token")).willReturn("new-hash");
        given(member.getId()).willReturn(1L);
        given(member.getOnboardingCompleted()).willReturn(true);
        given(memberTermRepository.areAllRequiredTermsAgreed(1L)).willReturn(true);

        MemberResDTO.TokenReissue result = memberService.tokenReissue(dto);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.onboardingCompleted()).isTrue();
        assertThat(result.termsAgreed()).isTrue();
        verify(member).updateRefreshTokenToHash("new-hash");
    }

    @Test
    void logout_회원이_없으면_예외() {
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.logout(1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND));
    }

    @Test
    void logout_정상_흐름이면_refreshToken을_삭제한다() {
        Member member = mock(Member.class);
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.of(member));

        memberService.logout(1L);

        verify(member).deleteRefreshToken();
    }

    @Test
    void withdraw_회원이_없으면_예외() {
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.withdraw(1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND));
    }

    @Test
    void withdraw_정상_흐름이면_이벤트를_발행한_후_회원을_삭제한다() {
        Member member = mock(Member.class);
        given(memberRepository.findByIdForUpdate(1L)).willReturn(Optional.of(member));

        memberService.withdraw(1L);

        InOrder inOrder = inOrder(applicationEventPublisher, memberRepository);
        inOrder.verify(applicationEventPublisher).publishEvent(any(MemberWithdrawnEvent.class));
        inOrder.verify(memberRepository).delete(member);
    }

    @Test
    void getMyInfo_회원이_없으면_예외() {
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMyInfo(1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND));
    }

    @Test
    void getMyInfo_storybookCharacter가_없으면_characterImageUrl은_null이다() {
        Member member = mock(Member.class);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(member.getStorybookCharacter()).willReturn(null);

        MemberResDTO.MyInfo result = memberService.getMyInfo(1L);

        assertThat(result.characterImageUrl()).isNull();
        verifyNoInteractions(storybookCharacterVariantRepository);
    }

    @Test
    void getMyInfo_캐릭터는_있지만_PROFILE_variant가_없으면_null을_반환한다() {
        Member member = mock(Member.class);
        StorybookCharacter character = mock(StorybookCharacter.class);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(member.getStorybookCharacter()).willReturn(character);
        given(storybookCharacterVariantRepository.findByStorybookCharacterAndVariant(character, Variant.PROFILE))
                .willReturn(Optional.empty());

        MemberResDTO.MyInfo result = memberService.getMyInfo(1L);

        assertThat(result.characterImageUrl()).isNull();
    }

    @Test
    void getMyInfo_캐릭터와_PROFILE_variant가_있으면_이미지_URL을_반환한다() {
        Member member = mock(Member.class);
        StorybookCharacter character = mock(StorybookCharacter.class);
        StorybookCharacterVariant variant = mock(StorybookCharacterVariant.class);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(member.getStorybookCharacter()).willReturn(character);
        given(storybookCharacterVariantRepository.findByStorybookCharacterAndVariant(character, Variant.PROFILE))
                .willReturn(Optional.of(variant));
        given(variant.getImageUrl()).willReturn("https://example.com/character.png");

        MemberResDTO.MyInfo result = memberService.getMyInfo(1L);

        assertThat(result.characterImageUrl()).isEqualTo("https://example.com/character.png");
    }
}