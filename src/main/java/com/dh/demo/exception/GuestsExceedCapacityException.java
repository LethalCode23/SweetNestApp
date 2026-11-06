package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** guests supera la capacidad de la unidad. */
public class GuestsExceedCapacityException extends BusinessRuleException {
    public GuestsExceedCapacityException(String message) {
        super(message, ApiResponseCode.GUESTS_EXCEED_CAPACITY, HttpStatus.BAD_REQUEST);
    }
}
