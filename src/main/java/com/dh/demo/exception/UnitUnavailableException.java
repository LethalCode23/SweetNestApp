package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** La unidad no esta libre en el rango pedido (Fase 2: cruce de fechas). */
public class UnitUnavailableException extends BusinessRuleException {
    public UnitUnavailableException(String message) {
        super(message, ApiResponseCode.UNIT_UNAVAILABLE, HttpStatus.CONFLICT);
    }
}
