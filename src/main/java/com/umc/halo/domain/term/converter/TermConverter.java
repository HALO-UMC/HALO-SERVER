package com.umc.halo.domain.term.converter;

import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.domain.term.entity.MemberTerm;
import com.umc.halo.domain.term.entity.Term;
import com.umc.halo.domain.member.entity.Member;

import java.util.List;

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

    public static TermResDTO.AgreementStatus toAgreementStatus(boolean termsAgreed, List<TermResDTO.AgreementInfo> agreements) {
        return TermResDTO.AgreementStatus.builder()
                .termsAgreed(termsAgreed)
                .agreements(agreements)
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