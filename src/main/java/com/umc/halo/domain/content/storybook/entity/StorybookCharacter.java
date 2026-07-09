package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.global.enums.Emotion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storybook_character")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorybookCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_character_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Variant variant;

    @Column
    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


}
