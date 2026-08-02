package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "storybook_character_variant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"storybook_character_id", "variant"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorybookCharacterVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_character_variant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_character_id", nullable = false)
    private StorybookCharacter storybookCharacter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Variant variant;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
