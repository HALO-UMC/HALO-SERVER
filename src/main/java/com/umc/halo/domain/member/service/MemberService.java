package com.umc.halo.domain.member.service;

import com.umc.halo.domain.content.storybook.entity.StorybookCharacterVariant;
import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.domain.content.storybook.repository.StorybookCharacterVariantRepository;
import com.umc.halo.domain.member.converter.MemberConverter;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.AuthErrorCode;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.oauth.AbstractOidcProvider;
import com.umc.halo.domain.member.oauth.OidcProviderFactory;
import com.umc.halo.domain.member.oauth.OidcUserInfo;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import com.umc.halo.global.util.HashUtil;
import com.umc.halo.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final NotificationRepository notificationRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final MemberTagRepository memberTagRepository;
    private final StorybookCharacterVariantRepository storybookCharacterVariantRepository;
    private final MemberDeviceRepository memberDeviceRepository;
    private final JwtUtil jwtUtil;
    private final HashUtil hashUtil;
    private final OidcProviderFactory oidcProviderFactory;
    private final MemberWriter memberWriter;

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public MemberResDTO.Login login(MemberReqDTO.Login dto) {

        Provider provider;

        try {
            provider = Provider.valueOf(dto.provider());
        } catch (IllegalArgumentException e) {
            throw new ProjectException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        AbstractOidcProvider oidcProvider = oidcProviderFactory.getProvider(provider);
        OidcUserInfo oidcUserInfo = oidcProvider.verify(dto.providerToken());

        // DB에 회원 저장
        return memberWriter.persist(provider, oidcUserInfo);
    }

    @Transactional
    public MemberResDTO.TokenReissue tokenReissue(MemberReqDTO.TokenReissue dto) {

        String refreshToken = dto.refreshToken();

        if (!jwtUtil.isValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new ProjectException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long memberId = jwtUtil.getMemberId(refreshToken);
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        if (!hashUtil.matches(refreshToken, member.getRefreshTokenHash())) {
            throw new ProjectException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.createAccessToken(memberId);
        String newRefreshToken = jwtUtil.createRefreshToken(memberId);
        member.updateRefreshTokenToHash(hashUtil.hash(newRefreshToken));

        boolean termsAgreed = memberTermRepository.areAllRequiredTermsAgreed(member.getId());

        return MemberConverter.toTokenReissueResponse(newAccessToken, newRefreshToken, member.getOnboardingCompleted(), termsAgreed);
    }

    @Transactional
    public void logout(Long memberId) {
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));
        member.deleteRefreshToken();
    }

    @Transactional
    public void withdraw(Long memberId) {

        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        memberChapterAnswerRepository.deleteByMemberChapterMemberId(memberId);
        memberChapterRepository.deleteByMemberId(memberId);
        memberStorybookRepository.deleteByMemberId(memberId);
        memberTermRepository.deleteByMemberId(memberId);
        memberSettingRepository.deleteByMemberId(memberId);
        notificationRepository.deleteByMemberId(memberId);
        anniversaryRepository.deleteByMemberId(memberId);
        memberTagRepository.deleteByMemberId(memberId);
        memberDeviceRepository.deleteByMemberId(memberId);

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public MemberResDTO.MyInfo getMyInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        String characterImageUrl = null;
        if (member.getStorybookCharacter() != null) {
            characterImageUrl = storybookCharacterVariantRepository
                    .findByStorybookCharacterAndVariant(member.getStorybookCharacter(), Variant.PROFILE)
                    .map(StorybookCharacterVariant::getImageUrl)
                    .orElseGet(() -> {
                        log.warn("[CharacterImageMissing] PROFILE variant 없음: characterId={}", member.getStorybookCharacter().getId());
                        return null;
                    });
        }

        return MemberConverter.toMyInfo(member, characterImageUrl);
    }

    @Transactional
    public void updateLastAccess(Long memberId) {

        Member member = memberRepository.findByIdForUpdate(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        member.updateLastAccessAt(LocalDateTime.now(ZONE_ID));

        List<Notification> notifications = notificationRepository.findByMemberIdAndNotificationTypeAndStatusIn(memberId, NotificationType.RETENTION, List.of(NotificationStatus.SCHEDULED));

        notifications.forEach(Notification::expire);
    }
}
