package com.umc.halo.domain.content.scenecard.entity;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scene_card")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SceneCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scene_card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;
}