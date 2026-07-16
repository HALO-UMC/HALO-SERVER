package com.umc.halo.domain.content.chapter.controller;

import com.umc.halo.domain.content.chapter.controller.docs.*;
import com.umc.halo.domain.content.chapter.dto.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.service.*;
import com.umc.halo.global.apiPayload.*;
import lombok.*;
import org.springframework.security.core.annotation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChapterController implements ChapterControllerDocs {

    private final ChapterService chapterService;

    // 오늘의 장 조회
    @Override
    @GetMapping("/v1/storybooks/{storybookId}/chapters/{chapterOrder}")
    public ApiResponse<ChapterResDTO.TodayChapter> getTodayChapter(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long storybookId,
            @PathVariable Integer chapterOrder
    ) {
        ChapterResDTO.TodayChapter result = chapterService.getTodayChapter(memberId, storybookId, chapterOrder);
        return ApiResponse.onSuccess(ChapterSuccessCode.TODAY_CHAPTER, result);
    }

}
