package com.umc.halo.domain.record.entity;

import com.umc.halo.domain.content.chapter.entity.ChapterQuestion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_chapter_answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChapterAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chapter_answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_chapter_id", nullable = false)
    private MemberChapter memberChapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_question_id", nullable = false)
    private ChapterQuestion chapterQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}