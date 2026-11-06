package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base de las excepciones de negocio del feature de reservas.
 * Lleva el codigo de ApiResponse y el HTTP que deben responderse.
 */
@Getter
public class BusinessRuleException extends RuntimeException {

    private final ApiResponseCode code;
    private final HttpStatus status;

    public BusinessRuleException(String message, ApiResponseCode code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
