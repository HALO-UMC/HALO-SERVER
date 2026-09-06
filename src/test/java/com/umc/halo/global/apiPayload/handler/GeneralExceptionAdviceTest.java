package com.umc.halo.global.apiPayload.handler;

import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GeneralExceptionAdviceTest {

    private final GeneralExceptionAdvice advice = new GeneralExceptionAdvice();

    @Test
    void handleProjectException은_AnniversaryErrorCode를_그대로_응답에_담는다() {
        ProjectException ex = new ProjectException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND);

        ResponseEntity<ApiResponse<Void>> response = advice.handleProjectException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("ANNIVERSARY404_1");
        assertThat(response.getBody().getMessage()).isEqualTo("존재하지 않는 기념일입니다.");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleProjectException은_SettingErrorCode를_그대로_응답에_담는다() {
        ProjectException ex = new ProjectException(SettingErrorCode.SETTING_NOT_FOUND);

        ResponseEntity<ApiResponse<Void>> response = advice.handleProjectException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("SETTING404_1");
        assertThat(response.getBody().getMessage()).isEqualTo("설정을 찾을 수 없습니다.");
    }

    @Test
    void handleProjectException은_AiErrorCode를_그대로_응답에_담는다() {
        ProjectException ex = new ProjectException(AiErrorCode.AI_RATE_LIMIT_EXCEEDED);

        ResponseEntity<ApiResponse<Void>> response = advice.handleProjectException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody().getCode()).isEqualTo("AI429_1");
        assertThat(response.getBody().getMessage()).isEqualTo("AI 요청 횟수를 초과했습니다. 한도 갱신 후 다시 시도해주세요.");
    }

    @Test
    void handleUnexpectedException은_500과_원인_미노출_메시지로_응답한다() {
        Exception ex = new RuntimeException("내부 DB 연결 실패 상세 정보");

        ResponseEntity<Object> response = advice.handleUnexpectedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(body.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다.");
        assertThat(body.getMessage()).doesNotContain("DB 연결 실패");
    }
}