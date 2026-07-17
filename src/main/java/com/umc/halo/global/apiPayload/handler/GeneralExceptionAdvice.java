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
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // @Validated 경로/쿼리 파라미터 검증
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("[ConstraintViolationException] 경로/쿼리 파라미터 검증 실패: {}", ex.getMessage());
        String detail = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, detail));

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
        log.warn("@Valid 검증에 실패했습니다.");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, errors));
    }

    // JSON 파싱 실패
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("JSON 파싱 실패: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, "요청 Body 형식이 잘못되었습니다."));
    }

    // 쿼리 파라미터 누락
    @Override
    protected @Nullable ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("필수 파라미터 누락: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 잘못된 Content-Type
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("지원하지 않는 Content-Type: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.UNSUPPORTED_MEDIA_TYPE;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 잘못된 Http 메소드
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("지원하지 않는 HTTP Method: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 타입 불일치
    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException matEx) {
            log.warn("타입 불일치: parameter={}, value={}", matEx.getName(), matEx.getValue());
        } else {
            log.warn("타입 불일치: property={}", ex.getPropertyName());
        }

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // 존재하지 않는 엔드포인트
    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("존재하지 않는 엔드포인트: {}", ex.getMessage());

        BaseErrorCode errorCode = GeneralErrorCode.NOT_FOUND;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    // Validation 실패
    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("[HandlerMethodValidationException] 컨트롤러 파라미터 검증 실패");
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                errors.put(parameterName, error.getDefaultMessage());
            });
        });

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, errors));
    }

}
