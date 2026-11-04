package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Unidad u hotel inactivo (state = 'I'). */
public class InactiveRecordException extends BusinessRuleException {
    public InactiveRecordException(String message) {
        super(message, ApiResponseCode.INACTIVE_RECORD, HttpStatus.CONFLICT);
    }
}
