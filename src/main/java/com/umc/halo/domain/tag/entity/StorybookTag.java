package com.umc.halo.domain.tag.entity;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.tag.enums.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "storybook_tag",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_storybook_tag_storybook_tag",
                        columnNames = {"storybook_id", "tag_id"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorybookTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(nullable = false, name = "priority_level")
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Column(nullable = false, length = 50)
    private String phrase;
}
