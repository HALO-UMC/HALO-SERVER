package com.umc.halo.domain.term.converter;

import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.domain.term.entity.MemberTerm;
import com.umc.halo.domain.term.entity.Term;
import com.umc.halo.domain.member.entity.Member;

public class TermConverter {

    public static TermResDTO.Info toInfo(Term term) {
        return TermResDTO.Info.builder()
                .termId(term.getId())
                .title(term.getTitle())
                .shortDescription(term.getShortDescription())
                .description(term.getDescription())
                .isRequired(term.getIsRequired())
                .updatedAt(term.getUpdatedAt())
                .build();
    }

    public static TermResDTO.AgreementStatus toAgreementStatus(boolean termsAgreed) {
        return TermResDTO.AgreementStatus.builder()
                .termsAgreed(termsAgreed)
                .build();
    }

    public static MemberTerm toMemberTerm(Member member, Term term, Boolean isAgreed) {
        return MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgreed(isAgreed)
                .build();
    }
}