package com.umc.halo.domain.tag.entity;

import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tag_combo_phrase",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tag_combo_phrase_tag1_tag2",
                        columnNames = {"tag_id_1", "tag_id_2"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagComboPhrase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_combo_phrase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id_1", nullable = false)
    private Tag tag1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id_2", nullable = false)
    private Tag tag2;

    @Column(nullable = false, length = 100)
    private String phrase;
}