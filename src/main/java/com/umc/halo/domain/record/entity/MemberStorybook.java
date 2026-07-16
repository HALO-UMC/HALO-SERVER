package com.umc.halo.domain.record.entity;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.global.entity.*;
import com.umc.halo.global.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "member_storybook")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberStorybook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_storybook_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(name = "last_chapter_order", nullable = false)
    private Integer lastChapterOrder;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    public void updateCompleted(Integer chapterOrder) {
        this.lastChapterOrder = chapterOrder;
        this.lastCompletedDate = LocalDate.now();
    }

    public void updateDraft(Integer chapterOrder) {
        this.lastChapterOrder = chapterOrder;
    }
}