package com.umc.halo.domain.relationship.converter;

import com.umc.halo.domain.relationship.dto.RelationshipResDTO;
import com.umc.halo.domain.tag.entity.Tag;

import java.util.List;

public class RelationshipConverter {
    public static RelationshipResDTO.TagInfo toTagInfo(Tag tag) {
        return RelationshipResDTO.TagInfo.builder()
                .tagId(tag.getId())
                .title(tag.getTitle())
                .description(tag.getDescription())
                .build();
    }
    public static RelationshipResDTO.Info toInfo(
            List<RelationshipResDTO.TagInfo> parentPersonalityTags,
            RelationshipResDTO.TagInfo currentRelationState,
            List<RelationshipResDTO.TagInfo> goalRelationships
    ) {
        return RelationshipResDTO.Info.builder()
                .parentPersonalityTags(parentPersonalityTags)
                .currentRelationState(currentRelationState)
                .goalRelationships(goalRelationships)
                .build();
    }
}