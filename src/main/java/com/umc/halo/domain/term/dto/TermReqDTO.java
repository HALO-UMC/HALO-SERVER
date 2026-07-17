package com.umc.halo.domain.term.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TermReqDTO {

    public record Agree(
            @Valid
            List<Agreement> agreements
    ) {}
    public record Agreement(
            @NotNull(message = "termId는 필수입니다.")
            Long termId,
            @NotNull(message = "isAgreed는 필수입니다.")
            Boolean isAgreed
    ) {}
}