package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.exception.ChapterException;
import com.umc.halo.domain.content.chapter.exception.code.ChapterErrorCode;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.chapter.service.ChapterService;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.exception.RecordException;
import com.umc.halo.domain.record.exception.code.RecordErrorCode;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 조회/검증을 트랜잭션 안에서 수행
@Component
@RequiredArgsConstructor
public class RecordValidationReader {

    private final MemberRepository memberRepository;
    private final ChapterRepository chapterRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final ChapterService chapterService;

    @Transactional(readOnly = true)
    public LoadedRecordContext load(Long memberId, Long chapterId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));

        Storybook storybook = chapter.getStorybook();

        MemberStorybook memberStorybook = memberStorybookRepository.findByStorybookAndMember(storybook, member)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER));

        MemberChapter memberChapter = memberChapterRepository.findByMemberAndChapter(member, chapter);

        // 오늘 이미 장을 완료했는지 검증
        if (memberStorybook.isCompletedToday()) {
            throw new RecordException(RecordErrorCode.ALREADY_COMPLETED_TODAY);
        }

        // 아직 열리지 않은 장 / 이미 완료한 장인지 검증
        chapterService.validateChapterStatus(member, storybook, chapter.getChapterOrder(), memberChapter, memberStorybook);

        return new LoadedRecordContext(member, chapter, memberChapter);
    }

    public record LoadedRecordContext(Member member, Chapter chapter, MemberChapter memberChapter) {
    }
}