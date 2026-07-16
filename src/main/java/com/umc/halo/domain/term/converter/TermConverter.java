package com.umc.halo.domain.term.converter;

import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.domain.term.entity.Term;

public class TermConverter {

    public static TermResDTO.Info toInfo(Term term) {
        return TermResDTO.Info.builder()
                .termId(term.getId())
                .title(term.getTitle())
                .shortDescription(term.getShortDescription())
                .isRequired(term.getIsRequired())
                .build();
    }

    public static TermResDTO.AgreementStatus toAgreementStatus(boolean termsAgreed) {
        return TermResDTO.AgreementStatus.builder()
                .termsAgreed(termsAgreed)
                .build();
    }
}