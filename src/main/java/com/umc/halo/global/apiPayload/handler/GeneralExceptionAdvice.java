package com.umc.halo.global.apiPayload.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

@RestControllerAdvice
public class GeneralExceptionAdvice {

	//프로젝트에서 발생한 에러
	@ExceptionHandler(ProjectException.class)
	public ResponseEntity<ApiResponse<Void>> handleProjectException(ProjectException ex) {
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus())
			.body(ApiResponse.onFailure(errorCode, null));
	}

	//@Valid 검증 실패
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errors.put(error.getField(), error.getDefaultMessage());
		});

		BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
		return ResponseEntity.status(code.getStatus())
			.body(ApiResponse.onFailure(code, errors));
	}

	//그 외
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
		BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(code.getStatus())
			.body(ApiResponse.onFailure(code, ex.getMessage()));
	}

}
