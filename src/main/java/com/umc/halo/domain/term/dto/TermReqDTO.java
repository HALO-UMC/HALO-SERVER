package com.umc.halo.domain.term.dto;

import java.util.List;

public class TermReqDTO {

    public record Agree(
            List<Agreement> agreements
    ) {}
    public record Agreement(
            Long termId,
            Boolean isAgreed
    ) {}
}