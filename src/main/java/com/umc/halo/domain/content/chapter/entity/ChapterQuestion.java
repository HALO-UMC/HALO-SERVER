package com.umc.halo.domain.content.chapter.entity;

import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapter_question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChapterQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(length = 255, nullable = false)
    private String question;
}