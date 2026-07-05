package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storybook_chapter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorybookChapter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_chapter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "chapter_order", nullable = false)
    private Integer chapterOrder;
}