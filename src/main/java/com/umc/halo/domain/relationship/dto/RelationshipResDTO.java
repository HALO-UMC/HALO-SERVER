package com.umc.halo.domain.relationship.dto;

import lombok.Builder;
import java.util.List;

public class RelationshipResDTO {

    @Builder
    public record Info(
            List<TagInfo> parentPersonalityTags,
            TagInfo currentRelationState,
            List<TagInfo> goalRelationships
    ) {}

    @Builder
    public record TagInfo(
            Long tagId,
            String title,
            String description
    ) {}
}