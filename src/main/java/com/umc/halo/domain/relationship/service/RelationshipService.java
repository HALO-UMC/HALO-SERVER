package com.umc.halo.domain.relationship.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.relationship.converter.RelationshipConverter;
import com.umc.halo.domain.relationship.dto.RelationshipResDTO;
import com.umc.halo.domain.tag.entity.MemberTag;
import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final MemberRepository memberRepository;
    private final MemberTagRepository memberTagRepository;

    @Transactional(readOnly = true)
    public RelationshipResDTO.Info getRelationshipInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<RelationshipResDTO.TagInfo> parentPersonalityTags = new ArrayList<>();
        RelationshipResDTO.TagInfo currentRelationState = null;
        List<RelationshipResDTO.TagInfo> goalRelationships = new ArrayList<>();

        for (MemberTag memberTag : memberTagRepository.findAllByMember(member)) {
            Tag tag = memberTag.getTag();

            switch (tag.getCategory()) {
                case PARENT_TENDENCY -> parentPersonalityTags.add(RelationshipConverter.toTagInfo(tag));
                case CURRENT_RELATIONSHIP -> currentRelationState = RelationshipConverter.toTagInfo(tag);
                case DESIRED_DIRECTION -> goalRelationships.add(RelationshipConverter.toTagInfo(tag));
                default -> { /* 관계 정보와 무관한 카테고리는 무시하려고 넣음*/ }
            }
        }

        return RelationshipConverter.toInfo(parentPersonalityTags, currentRelationState, goalRelationships);
    }
}