package com.umc.halo.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

	@JsonProperty("isSuccess")
	private boolean isSuccess;

	@JsonProperty("code")
	private String code;

	@JsonProperty("message")
	private String message;

	@JsonProperty("result")
	private T result;

	//성공
	public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code, T result) {
		return new ApiResponse<>(true, code.getCode(), code.getMessage(), result);
	}

	public static ApiResponse<Void> onSuccess(BaseSuccessCode code) {
		return new ApiResponse<>(true, code.getCode(), code.getMessage(), null);
	}

	//실패
	public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T result) {
		return new ApiResponse<>(false, code.getCode(), code.getMessage(), result);
	}
}
