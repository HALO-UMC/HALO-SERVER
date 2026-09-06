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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @Test
    void handleHttpMessageNotReadable는_BAD_REQUEST와_고정_메시지로_응답한다() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(new RuntimeException("Unexpected character"));

        ResponseEntity<Object> response = advice.handleHttpMessageNotReadable(
                ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getResult()).isEqualTo("요청 Body 형식이 잘못되었습니다.");
    }

    @Test
    void handleMissingServletRequestParameter는_BAD_REQUEST로_응답한다() {
        MissingServletRequestParameterException ex = mock(MissingServletRequestParameterException.class);
        when(ex.getMessage()).thenReturn("Required request parameter 'page' is not present");

        ResponseEntity<Object> response = advice.handleMissingServletRequestParameter(
                ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.BAD_REQUEST.getCode());
    }

    @Test
    void handleHttpMediaTypeNotSupported는_UNSUPPORTED_MEDIA_TYPE으로_응답한다() {
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);
        when(ex.getMessage()).thenReturn("Content-Type 'text/plain' is not supported");

        ResponseEntity<Object> response = advice.handleHttpMediaTypeNotSupported(
                ex, HttpHeaders.EMPTY, HttpStatus.UNSUPPORTED_MEDIA_TYPE, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode());
    }

    @Test
    void handleHttpRequestMethodNotSupported는_METHOD_NOT_ALLOWED로_응답한다() {
        HttpRequestMethodNotSupportedException ex = mock(HttpRequestMethodNotSupportedException.class);
        when(ex.getMessage()).thenReturn("Request method 'DELETE' is not supported");

        ResponseEntity<Object> response = advice.handleHttpRequestMethodNotSupported(
                ex, HttpHeaders.EMPTY, HttpStatus.METHOD_NOT_ALLOWED, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.METHOD_NOT_ALLOWED.getCode());
    }

    @Test
    void handleTypeMismatch은_MethodArgumentTypeMismatchException이면_BAD_REQUEST로_응답한다() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("abc");
        when(ex.getName()).thenReturn("page");

        ResponseEntity<Object> response = advice.handleTypeMismatch(
                ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.BAD_REQUEST.getCode());
    }

    @Test
    void handleNoResourceFoundException은_NOT_FOUND로_응답한다() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getMessage()).thenReturn("No static resource no-such-path.");

        ResponseEntity<Object> response = advice.handleNoResourceFoundException(
                ex, HttpHeaders.EMPTY, HttpStatus.NOT_FOUND, mock(WebRequest.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(GeneralErrorCode.NOT_FOUND.getCode());
    }
}