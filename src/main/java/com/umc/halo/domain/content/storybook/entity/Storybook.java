package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storybook")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Storybook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(name = "theme_order", nullable = false)
    private Integer themeOrder;

    @Column(name = "short_description", length = 255, nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Column(name = "character_image_url", length = 255)
    private String characterImageUrl;
}