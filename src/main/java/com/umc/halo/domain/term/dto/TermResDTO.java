package com.umc.halo.domain.term.dto;

import lombok.Builder;

public class TermResDTO {

    @Builder
    public record Info(
            Long termId,
            String title,
            String shortDescription,
            Boolean isRequired
    ) {}

    @Builder
    public record AgreementStatus(
            Boolean termsAgreed
    ) {}
}