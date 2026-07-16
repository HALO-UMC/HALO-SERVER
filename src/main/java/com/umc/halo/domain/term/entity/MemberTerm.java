package com.umc.halo.domain.term.entity;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_term")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTerm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_term_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "is_agreed", nullable = false)
    @Builder.Default
    private Boolean isAgreed = false;

    public void updateIsAgreed(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }
}