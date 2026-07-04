package com.umc.halo.domain.setting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bgm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bgm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bgm_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(name = "audio_url", length = 255, nullable = false)
    private String audioUrl;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}