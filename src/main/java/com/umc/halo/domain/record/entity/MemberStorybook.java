package com.umc.halo.domain.record.entity;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "member_storybook")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}