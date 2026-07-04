package com.umc.halo.domain.tag.entity;

import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(length = 20, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private Subtitle subtitle;

    @Column(length = 255)
    private String description;

    public enum Category { PARENT_TENDENCY, CURRENT_RELATIONSHIP, DESIRED_DIRECTION, COMMUNICATION_LEVEL }
    public enum Subtitle { /* 세부값 확정되면 채우기 */ }
}