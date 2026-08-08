package com.umc.halo.domain.setting.entity;

import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bgm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Bgm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bgm_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;
}