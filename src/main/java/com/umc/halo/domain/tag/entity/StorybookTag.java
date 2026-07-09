package com.umc.halo.domain.tag.entity;

import com.umc.halo.domain.content.storybook.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storybook_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorybookTag {

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
