package com.umc.halo.domain.member.entity;

import com.umc.halo.domain.member.enums.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_guest_uuid", columnNames = {"guest_uuid"}),
                @UniqueConstraint(name = "uk_member_provider", columnNames = {"provider", "provider_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "guest_uuid", length = 36)
    private String guestUuid;

    @Column(length = 10, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "onboarding_completed", nullable = false)
    @Builder.Default
    private Boolean onboardingCompleted = false;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;
}