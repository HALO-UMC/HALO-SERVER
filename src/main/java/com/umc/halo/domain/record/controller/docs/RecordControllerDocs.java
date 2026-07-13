package com.umc.halo.domain.record.controller.docs;

import com.umc.halo.domain.record.dto.*;
import com.umc.halo.global.apiPayload.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장 API")
public interface RecordControllerDocs {

    // 장 기록 작성
    @Operation()
    ApiResponse<RecordResDTO.WriteChapterRecord> writeChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @RequestBody RecordReqDTO.WriteChapterRecord recordReqDTO
    );
}


