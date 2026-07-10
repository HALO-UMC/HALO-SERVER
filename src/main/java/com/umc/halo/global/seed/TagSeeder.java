package com.umc.halo.global.seed;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.tag.entity.*;
import com.umc.halo.domain.tag.enums.*;
import com.umc.halo.domain.tag.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TagSeeder {
    private final TagRepository tagRepository;
    private final MemberTagRepository memberTagRepository;
    private final StorybookTagRepository storybookTagRepository;
    private final MemberRepository memberRepository;
    private final StorybookRepository storybookRepository;

    @Transactional
    public List<Tag> seedTag() {
        List<Tag> tags = List.of(
                // 부모님 성향 (긍정)
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("배려심 깊은")
                        .subtitle(Subtitle.POSITIVE)
                        .build(),
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("낙천적인")
                        .subtitle(Subtitle.POSITIVE)
                        .build(),

                // 부모님 성향 (중립)
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("무뚝뚝한")
                        .subtitle(Subtitle.NEUTRAL)
                        .build(),
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("차분한")
                        .subtitle(Subtitle.NEUTRAL)
                        .build(),

                // 부모님 성향 (중립)
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("걱정 많은")
                        .subtitle(Subtitle.CAUTIOUS)
                        .build(),
                Tag.builder()
                        .category(Category.PARENT_TENDENCY)
                        .title("잔소리")
                        .subtitle(Subtitle.CAUTIOUS)
                        .build(),

                // 현재 관계 상태
                Tag.builder()
                        .category(Category.CURRENT_RELATIONSHIP)
                        .title("매우 가까운 편이에요")
                        .description("부모이기 전에 친구 같은, 비밀까지 나누는 사이")
                        .build(),
                Tag.builder()
                        .category(Category.CURRENT_RELATIONSHIP)
                        .title("대체로 좋은 편이에요")
                        .description("일상적인 안부를 나누며 서로를 존중해요")
                        .build(),

                // 원하는 관계 방향
                Tag.builder()
                        .category(Category.DESIRED_DIRECTION)
                        .title("부모님을 더 알고 싶어요")
                        .build(),
                Tag.builder()
                        .category(Category.DESIRED_DIRECTION)
                        .title("같이 보내는 시간을 만들고 싶어요")
                        .build(),
                Tag.builder()
                        .category(Category.DESIRED_DIRECTION)
                        .title("어색하지 않게 이야기하고 싶어요")
                        .build(),
                Tag.builder()
                        .category(Category.DESIRED_DIRECTION)
                        .title("마음을 표현해보고 싶어요")
                        .build(),
                Tag.builder()
                        .category(Category.DESIRED_DIRECTION)
                        .title("특별한 날이나 응원을 준비하고 싶어요")
                        .build()
        );

        List<Tag> savedTags = tagRepository.saveAll(tags);
        log.info("Tags {}건 시딩 완료", savedTags.size());

        return savedTags;
    }

    @Transactional
    public List<MemberTag> seedMemberTag(List<Member> members, List<Tag> tags) {
        Map<String, Member> member = members.stream()
                .collect(Collectors.toMap(Member::getName, Function.identity(), (a, b) -> a));
        Map<String, Tag> tag = tags.stream()
                .collect(Collectors.toMap(Tag::getTitle, Function.identity()));

        List<MemberTag> memberTags = List.of(
                // 김하로 - 부모님 성향 3개, 현재 관계 상태 1개, 원하는 관계 방향 2개
                MemberTag.builder().member(member.get("김하로")).tag(tag.get("배려심 깊은")).build(),
                MemberTag.builder().member(member.get("김하로")).tag(tag.get("낙천적인")).build(),
                MemberTag.builder().member(member.get("김하로")).tag(tag.get("무뚝뚝한")).build(),

                MemberTag.builder().member(member.get("김하로")).tag(tag.get("매우 가까운 편이에요")).build(),

                MemberTag.builder().member(member.get("김하로")).tag(tag.get("부모님을 더 알고 싶어요")).build(),
                MemberTag.builder().member(member.get("김하로")).tag(tag.get("어색하지 않게 이야기하고 싶어요")).build(),

                // 이온 - 부모님 성향 2개, 현재 관계 상태 1개, 원하는 관계 방향 2개
                MemberTag.builder().member(member.get("이온")).tag(tag.get("차분한")).build(),
                MemberTag.builder().member(member.get("이온")).tag(tag.get("걱정 많은")).build(),

                MemberTag.builder().member(member.get("이온")).tag(tag.get("대체로 좋은 편이에요")).build(),

                MemberTag.builder().member(member.get("이온")).tag(tag.get("같이 보내는 시간을 만들고 싶어요")).build(),
                MemberTag.builder().member(member.get("이온")).tag(tag.get("특별한 날이나 응원을 준비하고 싶어요")).build(),

                // 송하루(게스트) - 부모님 성향 1개, 지금의 관계 1개, 희망 관계 1개
                MemberTag.builder().member(member.get("송하루")).tag(tag.get("잔소리")).build(),

                MemberTag.builder().member(member.get("송하루")).tag(tag.get("대체로 좋은 편이에요")).build(),

                MemberTag.builder().member(member.get("송하루")).tag(tag.get("마음을 표현해보고 싶어요")).build()
        );

        List<MemberTag> savedMemberTags = memberTagRepository.saveAll(memberTags);
        log.info("MemberTags {}건 시딩 완료", savedMemberTags.size());

        return savedMemberTags;
    }

    @Transactional
    public List<StorybookTag> seedStorybookTag(List<Storybook> storybooks, List<Tag> tags) {
        Map<String, Storybook> storybook = storybooks.stream()
                .collect(Collectors.toMap(Storybook::getTitle, Function.identity()));
        Map<String, Tag> tag = tags.stream()
                .collect(Collectors.toMap(Tag::getTitle, Function.identity()));

        List<StorybookTag> storybookTags = List.of(
                // 오래 전 당신
                StorybookTag.builder()
                        .storybook(storybook.get("오래 전 당신"))
                        .tag(tag.get("어색하지 않게 이야기하고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("어색하지 않게 이야기하고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("오래 전 당신"))
                        .tag(tag.get("부모님을 더 알고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 어색하지 않게 이야기하고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("오래 전 당신"))
                        .tag(tag.get("같이 보내는 시간을 만들고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("같이 보내는 시간을 만들고, 어색하지 않게 이야기하고 싶어요")
                        .build(),

                // 당신 사용설명서
                StorybookTag.builder()
                        .storybook(storybook.get("당신 사용설명서"))
                        .tag(tag.get("부모님을 더 알고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("부모님을 더 알고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("당신 사용설명서"))
                        .tag(tag.get("같이 보내는 시간을 만들고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 같이 보내는 시간을 만들고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("당신 사용설명서"))
                        .tag(tag.get("특별한 날이나 응원을 준비하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 특별한 날이나 응원을 준비하고 싶어요")
                        .build(),

                // 가족의 온도
                StorybookTag.builder()
                        .storybook(storybook.get("가족의 온도"))
                        .tag(tag.get("부모님을 더 알고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("부모님을 더 알고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("가족의 온도"))
                        .tag(tag.get("마음을 표현해보고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 마음을 표현해보고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("가족의 온도"))
                        .tag(tag.get("어색하지 않게 이야기하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 어색하지 않게 이야기하고 싶어요")
                        .build(),

                // 취향이 닿는 날
                StorybookTag.builder()
                        .storybook(storybook.get("취향이 닿는 날"))
                        .tag(tag.get("같이 보내는 시간을 만들고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("같이 보내는 시간을 만들고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("취향이 닿는 날"))
                        .tag(tag.get("어색하지 않게 이야기하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("같이 보내는 시간을 만들고, 어색하지 않게 이야기하고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("취향이 닿는 날"))
                        .tag(tag.get("특별한 날이나 응원을 준비하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("같이 보내는 시간을 만들고, 특별한 날이나 응원을 준비하고 싶어요")
                        .build(),

                // 나란히 걷는 날
                StorybookTag.builder()
                        .storybook(storybook.get("나란히 걷는 날"))
                        .tag(tag.get("같이 보내는 시간을 만들고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("같이 보내는 시간을 만들고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("나란히 걷는 날"))
                        .tag(tag.get("마음을 표현해보고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("같이 보내는 시간을 만들고, 마음을 표현해보고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("나란히 걷는 날"))
                        .tag(tag.get("부모님을 더 알고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("부모님을 더 알고, 같이 보내는 시간을 만들고 싶어요")
                        .build(),

                // 오늘은 내가 먼저
                StorybookTag.builder()
                        .storybook(storybook.get("오늘은 내가 먼저"))
                        .tag(tag.get("마음을 표현해보고 싶어요"))
                        .priorityLevel(PriorityLevel.PRIMARY)
                        .phrase("마음을 표현해보고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("오늘은 내가 먼저"))
                        .tag(tag.get("특별한 날이나 응원을 준비하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("마음을 표현해보고, 특별한 날이나 응원을 준비하고 싶어요")
                        .build(),
                StorybookTag.builder()
                        .storybook(storybook.get("오늘은 내가 먼저"))
                        .tag(tag.get("어색하지 않게 이야기하고 싶어요"))
                        .priorityLevel(PriorityLevel.SECONDARY)
                        .phrase("어색하지 않게 이야기하고, 마음을 표현해보고 싶어요")
                        .build()
        );

        List<StorybookTag> savedStorybookTags = storybookTagRepository.saveAll(storybookTags);
        log.info("StorybookTags {}건 시딩 완료", savedStorybookTags.size());

        return savedStorybookTags;
    }

    @Transactional
    public void seed() {
        List<Member> members = memberRepository.findAll();
        List<Storybook> storybooks = storybookRepository.findAll();
        List<Tag> tags = seedTag();

        seedMemberTag(members, tags);
        seedStorybookTag(storybooks, tags);
    }
}