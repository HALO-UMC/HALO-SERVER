package com.umc.halo.domain.record.entity;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.scenecard.entity.SceneCard;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "member_chapter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(name = "cover_type", nullable = false)
    private CoverType coverType;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "image_key", length = 255)
    private String imageKey;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Emotion { CURIOUS, WARM, TOUCHED, AWKWARD, CLOSER }
    public enum CoverType { SCENE_CARD, IMAGE }
    public enum Status { DRAFT, COMPLETED }
}