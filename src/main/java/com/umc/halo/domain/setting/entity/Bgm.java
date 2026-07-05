package com.umc.halo.domain.setting.entity;

import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bgm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bgm extends BaseEntity {

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
}