package com.umc.halo.domain.member.entity;

import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.member.enums.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_provider", columnNames = {"provider", "provider_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_character_id")
    private StorybookCharacter storybookCharacter;

    @Column(length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "refresh_token_hash", length = 64)
    private String refreshTokenHash;

    @Column(name = "onboarding_completed", nullable = false)
    @Builder.Default
    private Boolean onboardingCompleted = false;

    @Column(name = "onboarding_step")
    private Integer onboardingStep;

    public void updateRefreshTokenToHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateGenderAndBirthDate(Gender gender, LocalDate birthDate) {
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void updateOnboardingStep(Integer step) {
        this.onboardingStep = step;
    }

    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }

    public void assignStorybookCharacter(StorybookCharacter storybookCharacter) {
        this.storybookCharacter = storybookCharacter;
    }

    public void deleteRefreshToken() {
        this.refreshTokenHash = null;
    }
}