package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface MemberChapterAnswerRepository extends JpaRepository<MemberChapterAnswer, Long> {

    List<MemberChapterAnswer> findByMemberChapter(MemberChapter memberChapter);

    @Query(
            """
                    SELECT mca FROM MemberChapterAnswer mca
                    JOIN FETCH mca.chapterQuestion cq
                    WHERE mca.memberChapter=:memberChapter
                    ORDER BY cq.questionOrder ASC
                    """)
    List<MemberChapterAnswer> findAllByMemberChapterOrderByQuestionOrder(@Param("memberChapter") MemberChapter memberChapter);

    void deleteAllByMemberChapter(MemberChapter memberChapter);

    void deleteByMemberChapterMemberId(Long memberId);
}
