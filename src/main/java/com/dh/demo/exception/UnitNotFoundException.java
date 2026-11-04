package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Unidad de hotel inexistente. */
public class UnitNotFoundException extends BusinessRuleException {
    public UnitNotFoundException(String message) {
        super(message, ApiResponseCode.UNIT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
