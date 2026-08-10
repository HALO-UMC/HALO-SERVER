package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.enums.Provider;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.dto.RecordReqDTO;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.content.chapter.exception.ChapterException;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.exception.RecordException;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.global.enums.Emotion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChapterRecordWriter#L2 관련(리뷰 지적) 실제 DB 기반 동시성 테스트: 같은 memberChapter(DRAFT)를
 * 두 요청이 동시에 COMPLETED로 쓰려고 할 때, 재검증 가드가 실제로 경합을 막는지 확인한다.
 * <p>
 * 실행하려면 DB_URL 등 환경변수로 실제 MySQL을 가리켜야 함.
 */
@SpringBootTest
class ChapterRecordWriterIntegrationTest {

    @Autowired private ChapterRecordWriter chapterRecordWriter;
    @Autowired private MemberRepository memberRepository;
    @Autowired private StorybookRepository storybookRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private MemberStorybookRepository memberStorybookRepository;
    @Autowired private MemberChapterRepository memberChapterRepository;
    @Autowired private MemberChapterAnswerRepository memberChapterAnswerRepository;

    private Long memberId;
    private Long storybookId;
    private Long chapterId;
    private Long memberStorybookId;
    private Long memberChapterId;

    @AfterEach
    void cleanUp() {
        if (memberChapterId != null) {
            memberChapterAnswerRepository.deleteByMemberChapterMemberId(memberId);
            memberChapterRepository.findById(memberChapterId).ifPresent(memberChapterRepository::delete);
        }
        if (memberStorybookId != null) {
            memberStorybookRepository.findById(memberStorybookId).ifPresent(memberStorybookRepository::delete);
        }
        if (chapterId != null) {
            chapterRepository.findById(chapterId).ifPresent(chapterRepository::delete);
        }
        if (storybookId != null) {
            storybookRepository.findById(storybookId).ifPresent(storybookRepository::delete);
        }
        if (memberId != null) {
            memberRepository.findById(memberId).ifPresent(memberRepository::delete);
        }
    }

    @Test
    void 같은_DRAFT_장을_동시에_완료하면_한쪽만_성공하고_다른쪽은_재검증에서_막힌다() throws Exception {
        Member member = memberRepository.save(Member.builder()
                .provider(Provider.KAKAO)
                .providerId("race-chapter-" + System.nanoTime())
                .build());
        memberId = member.getId();

        Storybook storybook = storybookRepository.save(Storybook.builder()
                .title("race-storybook")
                .recommendationPhrase("phrase")
                .shortDescription("short")
                .description("desc")
                .imageUrl("http://example.com/a.png")
                .build());
        storybookId = storybook.getId();

        Chapter chapter = chapterRepository.save(Chapter.builder()
                .storybook(storybook)
                .title("race-chapter")
                .chapterOrder(1)
                .shortImageUrl("http://example.com/short.png")
                .longImageUrl("http://example.com/long.png")
                .shortDescription("short-desc")
                .imageDescription("image-desc")
                .build());
        chapterId = chapter.getId();

        MemberStorybook memberStorybook = memberStorybookRepository.save(MemberStorybook.builder()
                .member(member)
                .storybook(storybook)
                .lastChapterOrder(1)
                .startedDate(LocalDate.now())
                .build());
        memberStorybookId = memberStorybook.getId();

        MemberChapter memberChapter = memberChapterRepository.save(MemberChapter.builder()
                .member(member)
                .chapter(chapter)
                .status(Status.DRAFT)
                .build());
        memberChapterId = memberChapter.getId();

        RecordReqDTO.WriteChapterRecord dto = new RecordReqDTO.WriteChapterRecord(
                chapterId, Emotion.HAPPY, null, null, null, null, Status.COMPLETED);
        ValidatedChapterRecord validated = new ValidatedChapterRecord(memberChapterId, null, null, null, null);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Object> task = () -> {
            ready.countDown();
            start.await();
            return chapterRecordWriter.persist(memberId, dto, validated);
        };

        int successCount = 0;
        int blockedCount = 0;
        try {
            Future<Object> f1 = executor.submit(task);
            Future<Object> f2 = executor.submit(task);

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            for (Future<Object> f : List.of(f1, f2)) {
                try {
                    f.get(15, TimeUnit.SECONDS);
                    successCount++;
                } catch (ExecutionException e) {
                    // ALREADY_COMPLETED_TODAY(RecordException) 또는 COMPLETED_CHAPTER(ChapterException)
                    // 둘 중 하나로 막혀야 함 - 타이밍에 따라 어느 체크가 먼저 걸리는지 달라질 수 있음
                    assertThat(e.getCause()).isInstanceOfAny(RecordException.class, ChapterException.class);
                    blockedCount++;
                }
            }
        } finally {
            executor.shutdown();
            assertThat(executor.awaitTermination(15, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(successCount).isEqualTo(1);
        assertThat(blockedCount).isEqualTo(1);

        MemberChapter reloaded = memberChapterRepository.findById(memberChapterId).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(Status.COMPLETED);
    }
}