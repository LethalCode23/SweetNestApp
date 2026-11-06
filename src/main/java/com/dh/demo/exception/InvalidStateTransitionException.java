package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Transicion de estado de reserva no permitida (ej: confirmar algo que no esta PENDING). */
public class InvalidStateTransitionException extends BusinessRuleException {
    public InvalidStateTransitionException(String message) {
        super(message, ApiResponseCode.INVALID_STATE_TRANSITION, HttpStatus.CONFLICT);
    }
}
