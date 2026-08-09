package com.dh.demo.dto.response;

import com.dh.demo.common.ApiResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final int code;
    private final T data;

    private ApiResponse(boolean success, String message, int code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, ApiResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ApiResponse<T> success(String message, ApiResponseCode code, T data) {
        return new ApiResponse<>(true, message, code.getCode(), data);
    }

    public static <T> ApiResponse<T> error(String message, ApiResponseCode code) {
        return new ApiResponse<>(false, message, code.getCode(), null);
    }
}