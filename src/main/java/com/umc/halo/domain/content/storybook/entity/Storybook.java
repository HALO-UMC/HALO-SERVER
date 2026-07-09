package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.global.entity.*;
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

    @Column(name = "spine_color", length = 7, nullable = false)
    private String spineColor;

}