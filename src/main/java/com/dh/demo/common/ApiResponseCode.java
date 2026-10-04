package com.dh.demo.common;

import lombok.Getter;

@Getter
public enum ApiResponseCode {

    SUCCESS(0),
    VALIDATION_ERROR(1001),
    EMAIL_ALREADY_EXISTS(1002),
    INVALID_CREDENTIALS(1003),
    USER_NOT_FOUND(1004),
    TOKEN_EXPIRED(1005),
    ACCOUNT_LOCKED(1006),
    HOTEL_NOT_FOUND(1007),
    EMAIL_SENDING_ERROR(9001),
    INTERNAL_ERROR(9000);

    private final int code;
    ApiResponseCode(int code) { this.code = code; }
}