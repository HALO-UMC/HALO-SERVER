package com.umc.halo.domain.content.storybook.entity;

import com.umc.halo.domain.content.storybook.enums.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storybook_character",
        uniqueConstraints = @UniqueConstraint(columnNames = {"storybook_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorybookCharacter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storybook_character_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(length = 10, nullable = false)
    private String name;


}
