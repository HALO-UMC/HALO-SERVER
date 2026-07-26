package com.umc.halo.domain.record.entity;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.global.entity.*;
import com.umc.halo.global.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(
        name = "member_chapter",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "storybook_chapter_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberChapter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chapter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_chapter_id", nullable = false)
    private StorybookChapter storybookChapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_card_id")
    private SceneCard sceneCard;

    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    @Enumerated(EnumType.STRING)
    @Column(name = "cover_type")
    private CoverType coverType;

    @Column(name = "image_key", length = 255)
    private String imageKey;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public void updateRecord(StorybookChapter storybookChapter, SceneCard sceneCard, Emotion emotion,
                             CoverType coverType, String imageKey, Status status) {

        this.storybookChapter = storybookChapter;
        this.sceneCard = sceneCard;
        this.emotion = emotion;
        this.coverType = coverType;
        this.imageKey = imageKey;
        this.status = status;
        this.completedDate = (status == Status.COMPLETED) ? LocalDate.now() : null;
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }
}