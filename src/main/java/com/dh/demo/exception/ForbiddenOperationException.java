package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** El usuario no es dueño de la reserva ni administrador. */
public class ForbiddenOperationException extends BusinessRuleException {
    public ForbiddenOperationException(String message) {
        super(message, ApiResponseCode.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
    }
}
