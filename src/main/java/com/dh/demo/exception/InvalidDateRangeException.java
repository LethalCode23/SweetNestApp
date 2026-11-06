package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** checkOut no es posterior a checkIn. */
public class InvalidDateRangeException extends BusinessRuleException {
    public InvalidDateRangeException(String message) {
        super(message, ApiResponseCode.INVALID_DATE_RANGE, HttpStatus.BAD_REQUEST);
    }
}
