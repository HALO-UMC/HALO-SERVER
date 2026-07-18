package com.umc.halo.global.apiPayload.handler;

import com.umc.halo.global.apiPayload.*;
import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;
import jakarta.validation.*;
import lombok.extern.slf4j.*;
import org.jspecify.annotations.*;
import org.springframework.beans.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.validation.*;
import org.springframework.web.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.method.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.servlet.resource.*;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {

    // 프로젝트에서 발생한 에러
    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectException(ProjectException ex) {
        BaseErrorCode errorCode = ex.getErrorCode();
        log.warn("[{}] {}: {}", ex.getClass().getSimpleName(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // @Validated 경로/쿼리 파라미터 검증
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("[ConstraintViolationException] 경로/쿼리 파라미터 검증 실패: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf('.') + 1);
            errors.put(field, violation.getMessage());
        });

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, errors));

    }

    // 유니크 제약
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("[DataIntegrityViolationException] DB 제약조건 위배: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.CONFLICT;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // ModelAttribute 검증 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex) {
        log.warn("[BindException] ModelAttribute 바인딩 혹은 검증 실패: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, errors));
    }

    // 서버 내부 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception ex) {
        log.error("[UnexpectedException] 서버 내부 에러: ", ex);

        BaseErrorCode errorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));

    }

    // 오버라이드
    // @Valid(DTO 검증 실패)
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[MethodArgumentNotValidException] @Valid 검증 실패: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, errors));
    }

    // JSON 파싱 실패
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[HttpMessageNotReadableException] JSON 파싱 실패: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, "요청 Body 형식이 잘못되었습니다."));
    }

    // 쿼리 파라미터 누락
    @Override
    protected @Nullable ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[MissingServletRequestParameterException] 필수 파라미터 누락: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 잘못된 Content-Type
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[HttpMediaTypeNotSupportedException] 지원하지 않는 Content-Type: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.UNSUPPORTED_MEDIA_TYPE;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 잘못된 Http 메소드
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[HttpRequestMethodNotSupportedException] 지원하지 않는 HTTP Method: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 타입 불일치
    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException matEx) {
            log.warn("[MethodArgumentTypeMismatchException] 타입 불일치: parameter={}, value={}", matEx.getName(), matEx.getValue());
        } else {
            log.warn("[TypeMismatchException] 타입 불일치: property={}", ex.getPropertyName());
        }

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 존재하지 않는 엔드포인트
    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[NoResourceFoundException] 존재하지 않는 엔드포인트: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.NOT_FOUND;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 컨트롤러 파라미터 검증 (클래스 레벨 @Validated 없을 때 스프링 기본 메소드 검증)
    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[HandlerMethodValidationException] 컨트롤러 파라미터 검증 실패: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                errors.put(parameterName, error.getDefaultMessage());
            });
        });

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .headers(headers)
                .body(ApiResponse.onFailure(errorCode, errors));
    }

    // 오버라이드 되지 않은 나머지 ResponseEntityExceptionHandler 예외
    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.warn("[{}] {} - {}", ex.getClass().getSimpleName(), statusCode, ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "UNHANDLED_" + ex.getClass().getSimpleName(),
                "요청을 처리할 수 없습니다.",
                null
        );

        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(response);
    }
}
