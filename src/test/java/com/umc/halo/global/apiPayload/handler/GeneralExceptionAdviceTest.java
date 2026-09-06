package com.umc.halo.global.apiPayload.handler;

import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeneralExceptionAdviceTest {

    private final GeneralExceptionAdvice advice = new GeneralExceptionAdvice();

    private void dummyTarget(String title) {}

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

    @Test
    void handleConstraintViolationException은_필드명과_메시지를_추출해서_담는다() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("getAnniversary.id");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("양수여야 합니다.");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<Object> response = advice.handleConstraintViolationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.getResult();
        assertThat(errors).containsEntry("id", "양수여야 합니다.");
    }

    @Test
    void handleBindException은_필드_에러를_추출해서_담는다() {
        BindException ex = new BindException(new Object(), "anniversaryReq");
        ex.getBindingResult().addError(new FieldError("anniversaryReq", "title", "제목은 필수입니다."));

        ResponseEntity<Object> response = advice.handleBindException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.getResult();
        assertThat(errors).containsEntry("title", "제목은 필수입니다.");
    }

    @Test
    void handleMethodArgumentNotValid는_필드_에러를_추출해서_담는다() throws NoSuchMethodException {
        Method dummyMethod = GeneralExceptionAdviceTest.class.getDeclaredMethod("dummyTarget", String.class);
        MethodParameter parameter = new MethodParameter(dummyMethod, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "title", "제목은 필수입니다."));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = advice.handleMethodArgumentNotValid(
                ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.getResult();
        assertThat(errors).containsEntry("title", "제목은 필수입니다.");
    }

    @Test
    void handleHandlerMethodValidationException은_파라미터별_에러_메시지를_담는다() {
        HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);
        ParameterValidationResult result = mock(ParameterValidationResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        when(ex.getParameterValidationResults()).thenReturn(List.of(result));
        when(result.getMethodParameter()).thenReturn(parameter);
        when(parameter.getParameterName()).thenReturn("size");
        when(result.getResolvableErrors()).thenReturn(List.of(
                new DefaultMessageSourceResolvable(new String[]{"size"}, "size는 양수여야 합니다.")
        ));

        ResponseEntity<Object> response = advice.handleHandlerMethodValidationException(
                ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.getResult();
        assertThat(errors).containsEntry("size", "size는 양수여야 합니다.");
    }
}