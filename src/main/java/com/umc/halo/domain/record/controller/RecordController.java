package com.umc.halo.domain.record.controller;

import com.umc.halo.domain.record.controller.docs.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.excption.code.*;
import com.umc.halo.domain.record.service.*;
import com.umc.halo.global.apiPayload.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecordController implements RecordControllerDocs {

    private final RecordService recordService;

    @Override
    @PostMapping("/v1/member-chapters")
    public ApiResponse<RecordResDTO.WriteChapterRecord> writeChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody RecordReqDTO.WriteChapterRecord recordReqDTO
    ) {
        RecordResDTO.WriteChapterRecord result = recordService.writeChapterRecord(memberId, recordReqDTO);
        return ApiResponse.onSuccess(RecordSuccessCode.WRITE_CHAPTER_RECORD, result);
    }

    @Override
    @GetMapping("/v1/member-chapters/{memberChapterId}")
    public ApiResponse<RecordResDTO.ReadChapterRecord> readChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long memberChapterId
    ) {
        RecordResDTO.ReadChapterRecord result = recordService.readChapterRecord(memberId, memberChapterId);
        return ApiResponse.onSuccess(RecordSuccessCode.READ_CHAPTER_RECORD, result);
    }


}
