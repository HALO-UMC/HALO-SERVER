package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.tag.entity.MemberTag;
import com.umc.halo.domain.tag.entity.StorybookTag;
import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.domain.tag.enums.Category;
import com.umc.halo.domain.tag.enums.PriorityLevel;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import com.umc.halo.domain.tag.repository.StorybookTagRepository;
import com.umc.halo.domain.tag.repository.TagRepository;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * StorybookService의 5개 조회/등록 메서드에 대한 단위 테스트.
 * getStorybookDetail / startStorybook / getStorybookList / getRecommendedStorybooks / getHome
 */
@ExtendWith(MockitoExtension.class)
class StorybookServiceTest {

    @Mock
    private StorybookRepository storybookRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private MemberChapterRepository memberChapterRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberStorybookRepository memberStorybookRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private StorybookTagRepository storybookTagRepository;
    @Mock
    private MemberTagRepository memberTagRepository;

    @InjectMocks
    private StorybookService storybookService;

    private Member member(Long id) {
        return Member.builder().id(id).name("혜담").build();
    }

    private Storybook storybook(Long id) {
        return Storybook.builder()
                .id(id)
                .title("스토리북" + id)
                .recommendationPhrase("추천 문구" + id)
                .shortDescription("한줄 설명" + id)
                .description("상세 설명" + id)
                .imageUrl("https://cdn.example.com/" + id + ".png")
                .build();
    }

    private Chapter chapter(Storybook storybook, int order) {
        return Chapter.builder()
                .id((long) order)
                .storybook(storybook)
                .title("챕터" + order)
                .chapterOrder(order)
                .shortImageUrl("short.png")
                .longImageUrl("long.png")
                .shortDescription("짧은 설명")
                .description("설명")
                .imageDescription("이미지 설명")
                .build();
    }

    // ===== getStorybookDetail =====

    @Test
    void 스토리북_상세조회시_회원이_없으면_예외를_던진다() {
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storybookService.getStorybookDetail(1L, 1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND));

        verifyNoInteractions(storybookRepository);
    }

    @Test
    void 스토리북_상세조회시_스토리북이_없으면_예외를_던진다() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(member(1L)));
        given(storybookRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storybookService.getStorybookDetail(1L, 1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(StorybookErrorCode.NOT_FOUND));
    }

    @Test
    void 스토리북_상세조회시_챕터별_상태와_진행률을_올바르게_계산한다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);
        Chapter chapter3 = chapter(storybook, 3);

        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(1L))
                .willReturn(List.of(chapter1, chapter2, chapter3));
        given(memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, 1L))
                .willReturn(List.of(completedChapter));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.empty());

        StorybookResDTO.GetStorybookDetail result = storybookService.getStorybookDetail(1L, 1L);

        assertThat(result.completedChapterCount()).isEqualTo(1);
        assertThat(result.progressPercentage()).isEqualTo(10);
        assertThat(result.chapters()).hasSize(3);
        assertThat(result.chapters().get(0).status()).isEqualTo(ChapterViewStatus.COMPLETED);
        assertThat(result.chapters().get(0).memberChapterId()).isEqualTo(100L);
        assertThat(result.chapters().get(1).status()).isEqualTo(ChapterViewStatus.TODAY);
        assertThat(result.chapters().get(2).status()).isEqualTo(ChapterViewStatus.LOCKED);
    }

    @Test
    void 스토리북_상세조회시_오늘_이미_완료했으면_다음_장은_TODAY_LOCKED다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);

        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();

        // 오늘 이미 한 장을 완료해서 lastCompletedDate가 오늘인 상태
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(2).startedDate(LocalDate.now())
                .lastCompletedDate(LocalDate.now())
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(1L))
                .willReturn(List.of(chapter1, chapter2));
        given(memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, 1L))
                .willReturn(List.of(completedChapter));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.of(memberStorybook));

        StorybookResDTO.GetStorybookDetail result = storybookService.getStorybookDetail(1L, 1L);

        assertThat(result.chapters().get(0).status()).isEqualTo(ChapterViewStatus.COMPLETED);
        assertThat(result.chapters().get(1).status()).isEqualTo(ChapterViewStatus.TODAY_LOCKED);
    }

    // ===== startStorybook =====

    @Test
    void 스토리북_시작시_정상_흐름이면_진행중_상태로_저장한다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.empty());

        StorybookResDTO.StartStorybook result = storybookService.startStorybook(1L, 1L);

        assertThat(result.storybookId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo(StorybookStatus.IN_PROGRESS);

        ArgumentCaptor<MemberStorybook> captor = ArgumentCaptor.forClass(MemberStorybook.class);
        verify(memberStorybookRepository).save(captor.capture());
        MemberStorybook saved = captor.getValue();
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getStorybook()).isEqualTo(storybook);
        assertThat(saved.getLastChapterOrder()).isEqualTo(1);
        assertThat(saved.getStartedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void 스토리북_시작시_이미_완료한_스토리북이면_예외를_던진다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.of(MemberStorybook.builder().id(1L).member(member).storybook(storybook)
                        .lastChapterOrder(1).startedDate(LocalDate.now()).build()));
        given(chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(1L)).willReturn(List.of(chapter1));
        given(memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, 1L))
                .willReturn(List.of(completedChapter));

        assertThatThrownBy(() -> storybookService.startStorybook(1L, 1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(StorybookErrorCode.ALREADY_COMPLETED));

        verify(memberStorybookRepository, never()).save(any());
    }

    @Test
    void 스토리북_시작시_이미_진행중인_스토리북이면_예외를_던진다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);
        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.of(MemberStorybook.builder().id(1L).member(member).storybook(storybook)
                        .lastChapterOrder(1).startedDate(LocalDate.now()).build()));
        given(chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(1L)).willReturn(List.of(chapter1, chapter2));
        given(memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, 1L))
                .willReturn(List.of(completedChapter));

        assertThatThrownBy(() -> storybookService.startStorybook(1L, 1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(StorybookErrorCode.ALREADY_IN_PROGRESS));

        verify(memberStorybookRepository, never()).save(any());
    }

    @Test
    void 스토리북_시작시_동시_저장으로_유니크_제약이_깨지면_이미_진행중_예외로_변환한다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findById(1L)).willReturn(Optional.of(storybook));
        given(memberStorybookRepository.findByMemberAndStorybook(member, storybook))
                .willReturn(Optional.empty());
        given(memberStorybookRepository.save(any()))
                .willThrow(new DataIntegrityViolationException("duplicate key"));

        assertThatThrownBy(() -> storybookService.startStorybook(1L, 1L))
                .isInstanceOf(ProjectException.class)
                .satisfies(e -> assertThat(((ProjectException) e).getErrorCode()).isEqualTo(StorybookErrorCode.ALREADY_IN_PROGRESS));
    }

    // ===== getStorybookList =====

    @Test
    void 스토리북_목록조회시_시작하지_않은_스토리북은_NOT_STARTED다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of());

        StorybookResDTO.GetStorybookList result = storybookService.getStorybookList(1L);

        assertThat(result.storybooks()).hasSize(1);
        assertThat(result.storybooks().get(0).storybookId()).isEqualTo(1L);
        assertThat(result.storybooks().get(0).status()).isEqualTo(StorybookStatus.NOT_STARTED);
        assertThat(result.storybooks().get(0).lastChapterOrder()).isNull();
    }

    @Test
    void 스토리북_목록조회시_진행중인_스토리북은_IN_PROGRESS이고_다음_챕터_순서를_보여준다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);
        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(1).startedDate(LocalDate.now()).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(memberStorybook));
        given(memberChapterRepository.findAllByMemberWithChapter(member)).willReturn(List.of(completedChapter));
        given(chapterRepository.findByStorybook_IdIn(List.of(1L))).willReturn(List.of(chapter1, chapter2));

        StorybookResDTO.GetStorybookList result = storybookService.getStorybookList(1L);

        StorybookResDTO.StorybookSummary summary = result.storybooks().get(0);
        assertThat(summary.status()).isEqualTo(StorybookStatus.IN_PROGRESS);
        assertThat(summary.lastChapterOrder()).isEqualTo(2);
        assertThat(summary.startedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void 스토리북_목록조회시_전체_챕터를_완료했으면_COMPLETED다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(1).startedDate(LocalDate.now()).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(memberStorybook));
        given(memberChapterRepository.findAllByMemberWithChapter(member)).willReturn(List.of(completedChapter));
        // 전체 챕터 수 == 완료한 챕터 수(1) 이므로 COMPLETED가 되어야 함
        given(chapterRepository.findByStorybook_IdIn(List.of(1L))).willReturn(List.of(chapter1));

        StorybookResDTO.GetStorybookList result = storybookService.getStorybookList(1L);

        assertThat(result.storybooks().get(0).status()).isEqualTo(StorybookStatus.COMPLETED);
    }

    @Test
    void 스토리북_목록조회시_오늘_이미_완료했지만_전체_완료는_아니면_TODAY_DONE이다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);
        MemberChapter completedChapter = MemberChapter.builder()
                .id(100L).member(member).chapter(chapter1).status(Status.COMPLETED).build();
        // 오늘 한 장을 완료해서 lastCompletedDate가 오늘인 상태 (전체 2장 중 1장만 완료)
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(1).startedDate(LocalDate.now())
                .lastCompletedDate(LocalDate.now())
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(memberStorybook));
        given(memberChapterRepository.findAllByMemberWithChapter(member)).willReturn(List.of(completedChapter));
        given(chapterRepository.findByStorybook_IdIn(List.of(1L))).willReturn(List.of(chapter1, chapter2));

        StorybookResDTO.GetStorybookList result = storybookService.getStorybookList(1L);

        assertThat(result.storybooks().get(0).status()).isEqualTo(StorybookStatus.TODAY_DONE);
    }

    // ===== getRecommendedStorybooks =====

    @Test
    void 추천_스토리북조회시_원하는_태그가_없으면_시작하지_않은_스토리북으로_보충한다() {
        Member member = member(1L);
        Storybook storybook1 = storybook(1L);
        Storybook storybook2 = storybook(2L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of());
        given(storybookRepository.findAll()).willReturn(List.of(storybook1, storybook2));

        StorybookResDTO.GetRecommendedStorybooks result = storybookService.getRecommendedStorybooks(1L);

        assertThat(result.storybooks()).hasSize(2);
        assertThat(result.storybooks().get(0).storybookId()).isEqualTo(1L);
        assertThat(result.storybooks().get(1).storybookId()).isEqualTo(2L);
    }

    @Test
    void 추천_스토리북조회시_원하는_태그가_1개면_PRIMARY_매칭된_스토리북을_최대_2개까지만_추천한다() {
        Member member = member(1L);
        Tag tagX = Tag.builder().id(1L).category(Category.DESIRED_DIRECTION).title("다정함").build();
        Storybook storybookA = storybook(10L);
        Storybook storybookB = storybook(20L);
        Storybook storybookC = storybook(30L);
        Storybook storybookD = storybook(40L);

        // A, B, D는 tagX가 PRIMARY로 매칭 / C는 SECONDARY로만 매칭 (카운트되면 안 됨)
        StorybookTag aPrimary = StorybookTag.builder().id(1L).tag(tagX).storybook(storybookA).priorityLevel(PriorityLevel.PRIMARY).build();
        StorybookTag bPrimary = StorybookTag.builder().id(2L).tag(tagX).storybook(storybookB).priorityLevel(PriorityLevel.PRIMARY).build();
        StorybookTag dPrimary = StorybookTag.builder().id(3L).tag(tagX).storybook(storybookD).priorityLevel(PriorityLevel.PRIMARY).build();
        StorybookTag cSecondary = StorybookTag.builder().id(4L).tag(tagX).storybook(storybookC).priorityLevel(PriorityLevel.SECONDARY).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of());
        given(memberTagRepository.findByMemberAndTag_Category(member, Category.DESIRED_DIRECTION))
                .willReturn(List.of(MemberTag.builder().id(1L).member(member).tag(tagX).build()));
        given(storybookTagRepository.findByTagIn(List.of(tagX)))
                .willReturn(List.of(aPrimary, bPrimary, dPrimary, cSecondary));

        StorybookResDTO.GetRecommendedStorybooks result = storybookService.getRecommendedStorybooks(1L);

        // PRIMARY 매칭이 3개(A,B,D)나 있어도 최대 2개까지만 반환
        assertThat(result.storybooks()).hasSize(2);
        // SECONDARY로만 매칭된 C(30L)는 절대 포함되면 안 됨
        assertThat(result.storybooks()).extracting(StorybookResDTO.RecommendedStorybook::storybookId)
                .doesNotContain(30L);
    }

    @Test
    void 추천_스토리북조회시_원하는_태그_2개가_매칭되면_콤보_조합_2개를_추천한다() {
        Member member = member(1L);
        Tag tagX = Tag.builder().id(1L).category(Category.DESIRED_DIRECTION).title("다정함").build();
        Tag tagY = Tag.builder().id(2L).category(Category.DESIRED_DIRECTION).title("솔직함").build();
        Storybook storybookA = storybook(10L);
        Storybook storybookB = storybook(20L);

        StorybookTag aPrimaryX = StorybookTag.builder().id(1L).tag(tagX).storybook(storybookA).priorityLevel(PriorityLevel.PRIMARY).build();
        StorybookTag aSecondaryY = StorybookTag.builder().id(2L).tag(tagY).storybook(storybookA).priorityLevel(PriorityLevel.SECONDARY).build();
        StorybookTag bPrimaryY = StorybookTag.builder().id(3L).tag(tagY).storybook(storybookB).priorityLevel(PriorityLevel.PRIMARY).build();
        StorybookTag bSecondaryX = StorybookTag.builder().id(4L).tag(tagX).storybook(storybookB).priorityLevel(PriorityLevel.SECONDARY).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of());
        given(memberTagRepository.findByMemberAndTag_Category(member, Category.DESIRED_DIRECTION))
                .willReturn(List.of(
                        MemberTag.builder().id(1L).member(member).tag(tagX).build(),
                        MemberTag.builder().id(2L).member(member).tag(tagY).build()
                ));
        given(storybookTagRepository.findByTagIn(List.of(tagX, tagY)))
                .willReturn(List.of(aPrimaryX, aSecondaryY, bPrimaryY, bSecondaryX));

        StorybookResDTO.GetRecommendedStorybooks result = storybookService.getRecommendedStorybooks(1L);

        assertThat(result.storybooks()).hasSize(2);
        assertThat(result.storybooks()).extracting(StorybookResDTO.RecommendedStorybook::storybookId)
                .containsExactlyInAnyOrder(10L, 20L);
    }

    // ===== getHome =====

    @Test
    void 홈조회시_진행중인_스토리북이_없으면_NO_STORYBOOK이고_추천_스토리북을_채운다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of());

        StorybookResDTO.GetHome result = storybookService.getHome(1L);

        assertThat(result.homeStatus()).isEqualTo(HomeStatus.NO_STORYBOOK);
        assertThat(result.memberName()).isEqualTo("혜담님");
        assertThat(result.inProgressStorybooks()).isEmpty();
        assertThat(result.bookshelf()).hasSize(1);
        assertThat(result.bookshelf().get(0).recommendationReasonText()).isEqualTo(storybook.getRecommendationPhrase());
        assertThat(result.recommendedStorybooks()).hasSize(1);
        assertThat(result.recommendedStorybooks().get(0).storybookId()).isEqualTo(1L);
    }

    @Test
    void 홈조회시_진행중인_스토리북이_1개면_IN_PROGRESS이고_추천은_비운다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(1).startedDate(LocalDate.now()).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(memberStorybook));
        given(chapterRepository.findByStorybook_IdIn(List.of(1L))).willReturn(List.of(chapter1));

        StorybookResDTO.GetHome result = storybookService.getHome(1L);

        assertThat(result.homeStatus()).isEqualTo(HomeStatus.IN_PROGRESS);
        assertThat(result.recommendedStorybooks()).isEmpty();
        assertThat(result.inProgressStorybooks()).hasSize(1);
        StorybookResDTO.InProgressStorybook inProgress = result.inProgressStorybooks().get(0);
        assertThat(inProgress.storybookId()).isEqualTo(1L);
        assertThat(inProgress.currentChapterOrder()).isEqualTo(1);
        assertThat(inProgress.todayAvailable()).isTrue();
        assertThat(result.bookshelf().get(0).currentChapterOrder()).isEqualTo(1);
    }

    @Test
    void 홈조회시_진행중인_스토리북이_2개_이상이면_MULTIPLE_IN_PROGRESS다() {
        Member member = member(1L);
        Storybook storybook1 = storybook(1L);
        Storybook storybook2 = storybook(2L);
        Chapter chapter1 = chapter(storybook1, 1);
        Chapter chapter2 = chapter(storybook2, 1);
        MemberStorybook ms1 = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook1)
                .lastChapterOrder(1).startedDate(LocalDate.now()).build();
        MemberStorybook ms2 = MemberStorybook.builder()
                .id(2L).member(member).storybook(storybook2)
                .lastChapterOrder(1).startedDate(LocalDate.now()).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook1, storybook2));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(ms1, ms2));
        given(chapterRepository.findByStorybook_IdIn(List.of(1L, 2L))).willReturn(List.of(chapter1, chapter2));

        StorybookResDTO.GetHome result = storybookService.getHome(1L);

        assertThat(result.homeStatus()).isEqualTo(HomeStatus.MULTIPLE_IN_PROGRESS);
        assertThat(result.inProgressStorybooks()).hasSize(2);
        assertThat(result.recommendedStorybooks()).isEmpty();
    }

    @Test
    void 홈조회시_진행중인_스토리북을_모두_오늘_완료했으면_ALL_COMPLETED_TODAY다() {
        Member member = member(1L);
        Storybook storybook = storybook(1L);
        Chapter chapter1 = chapter(storybook, 1);
        Chapter chapter2 = chapter(storybook, 2);
        // 전체 2장 중 아직 1장도 완료 안 했지만, 오늘 한도까지 진행해서 lastCompletedDate가 오늘로 찍힌 상태
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1L).member(member).storybook(storybook)
                .lastChapterOrder(1).startedDate(LocalDate.now())
                .lastCompletedDate(LocalDate.now())
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(storybookRepository.findAll()).willReturn(List.of(storybook));
        given(memberStorybookRepository.findByMember(member)).willReturn(List.of(memberStorybook));
        given(chapterRepository.findByStorybook_IdIn(List.of(1L))).willReturn(List.of(chapter1, chapter2));

        StorybookResDTO.GetHome result = storybookService.getHome(1L);

        assertThat(result.homeStatus()).isEqualTo(HomeStatus.ALL_COMPLETED_TODAY);
        assertThat(result.recommendedStorybooks()).isEmpty();
    }
}