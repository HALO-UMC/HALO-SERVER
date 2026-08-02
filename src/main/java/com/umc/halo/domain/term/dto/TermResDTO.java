package com.umc.halo.domain.term.dto;

import lombok.Builder;
import java.time.LocalDateTime;

public class TermResDTO {

    @Builder
    public record Info(
            Long termId,
            String title,
            String shortDescription,
            String description,
            Boolean isRequired,
            LocalDateTime updatedAt
    ) {}

    @Builder
    public record AgreementStatus(
            Boolean termsAgreed
    ) {}
}