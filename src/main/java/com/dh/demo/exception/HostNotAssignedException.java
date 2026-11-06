package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** El hotel de la operacion no tiene anfitrion asignado (hotUserSec null). */
public class HostNotAssignedException extends BusinessRuleException {
    public HostNotAssignedException(String message) {
        super(message, ApiResponseCode.HOST_NOT_ASSIGNED, HttpStatus.CONFLICT);
    }
}
