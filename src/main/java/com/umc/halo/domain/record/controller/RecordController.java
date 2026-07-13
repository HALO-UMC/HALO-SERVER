package com.umc.halo.domain.record.controller;

import com.umc.halo.domain.record.controller.docs.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.excption.code.*;
import com.umc.halo.domain.record.service.*;
import com.umc.halo.global.apiPayload.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecordController implements RecordControllerDocs {

    private final RecordService recordService;

    @Override
    @PostMapping("/v1/member-chapters")
    public ApiResponse<RecordResDTO.WriteChapterRecord> writeChapterRecord(
            Long memberId,
            RecordReqDTO.WriteChapterRecord recordReqDTO
    ) {
        RecordResDTO.WriteChapterRecord result = recordService.writeChapterRecord(memberId, recordReqDTO);
        return ApiResponse.onSuccess(RecordSuccessCode.MEMBER_CHAPTER, result);
    }

}
