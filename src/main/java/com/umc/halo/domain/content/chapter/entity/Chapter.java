package com.umc.halo.domain.content.chapter.entity;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chapter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(name = "chapter_order", nullable = false)
    private Integer chapterOrder;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Column(length = 255)
    private String guide;

    @Column(name = "short_description", length = 255, nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_description", length = 255, nullable = false)
    private String imageDescription;
}