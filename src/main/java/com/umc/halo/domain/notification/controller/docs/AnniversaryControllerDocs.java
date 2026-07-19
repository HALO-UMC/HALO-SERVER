package com.umc.halo.domain.notification.controller.docs;

import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "기념일 API")
public interface AnniversaryControllerDocs {

    @Operation(summary = "기념일 목록 조회 API",
            description = """
                    ### 기념일 목록 조회
                    마이페이지 내 기념일 관리 화면에서 사용자의 기념일 정보를 조회합니다.

                    **요청 형식**
                    - 별도의 요청 파라미터 없이 인증 토큰만으로 조회합니다.

                    **동작 방식**
                    1. 로그인한 회원이 등록한 기념일 목록을 조회합니다.
                    2. 반복 여부(isRepeated)와 음력이 아닌 기본 기념일을 기준으로 다가오는 기념일의 D-day를 계산합니다.
                    3. 음력 기반 기본 기념일(추석, 설날 등)은 연도별 환산 로직이 없어 D-day 계산 대상에서 제외되며, 기본 기념일 목록에는 그대로 노출됩니다.
                    4. 다가오는 기념일(upcomingAnniversaries), 내가 추가한 기념일(myAnniversaries), 기본 기념일(commonAnniversaries) 세 목록을 함께 반환합니다.
                    """)
    ApiResponse<AnniversaryResDTO.GetAnniversaries> getAnniversaries(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(summary = "기념일 추가 API",
            description = """
                    ### 기념일 추가
                    사용자가 새로운 기념일을 등록합니다.

                    **요청 형식**
```json
                    {
                      "title": "엄마랑 여행 가는 날",
                      "anniversaryDate": "2026-06-27",
                      "sevenDaysAlarmEnabled": true,
                      "dayAlarmEnabled": true,
                      "memo": "어머니랑 여행을 가기로 한 날, 처음으로 여행을 가기로 해서 너무 떨린다."
                    }
```

                    **동작 방식**
                    1. 기념일명, 날짜, 알림 설정을 필수로 입력받습니다.
                    2. 메모는 선택 입력이며 255자 이하로 제한됩니다.
                    3. 요청 값을 기반으로 기념일을 저장하고, 생성된 기념일의 ID를 반환합니다.
                    """)
    ApiResponse<AnniversaryResDTO.CreateAnniversary> createAnniversary(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AnniversaryReqDTO.Create request
    );

    @Operation(summary = "기념일 수정 API",
            description = """
                    ### 기념일 수정
                    사용자가 등록한 기념일 정보를 수정합니다.

                    **요청 형식**
                    - Path Variable: `anniversaryId` (수정할 기념일 ID)
```json
                    {
                      "title": "엄마랑 여행 가는 날",
                      "anniversaryDate": "2026-06-27",
                      "sevenDaysAlarmEnabled": true,
                      "dayAlarmEnabled": true,
                      "memo": "어머니랑 여행을 가기로 한 날, 처음으로 여행을 가기로 해서 너무 떨린다."
                    }
```

                    **동작 방식**
                    1. 요청한 기념일이 존재하는지 확인하고, 존재하지 않으면 404 예외를 반환합니다.
                    2. 해당 기념일이 로그인한 회원 소유인지 확인하고, 본인 소유가 아니면 403 예외를 반환합니다.
                    3. 검증을 통과하면 요청 값으로 기념일 정보를 갱신합니다.
                    """)
    ApiResponse<AnniversaryResDTO.UpdateAnniversary> updateAnniversary(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long anniversaryId,
            @Valid @RequestBody AnniversaryReqDTO.Update request
    );

    @Operation(summary = "기념일 삭제 API",
            description = """
                    ### 기념일 삭제
                    사용자가 등록한 기념일을 하나 이상 선택하여 한 번에 삭제합니다.

                    **요청 형식**
```json
                    {
                      "anniversaryIds": [1, 2, 5]
                    }
```

                    **동작 방식**
                    1. 요청한 ID 목록이 모두 존재하는지 확인하고, 하나라도 존재하지 않으면 404 예외를 반환합니다.
                    2. 요청한 ID가 모두 로그인한 회원 소유인지 확인하고, 하나라도 본인 소유가 아니면 403 예외를 반환합니다.
                    3. 검증을 통과하면 요청한 기념일을 모두 삭제합니다. (부분 삭제는 지원하지 않으며, 검증 실패 시 전체가 실패합니다.)
                    """)
    ApiResponse<Void> deleteAnniversaries(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AnniversaryReqDTO.Delete request
    );
}
