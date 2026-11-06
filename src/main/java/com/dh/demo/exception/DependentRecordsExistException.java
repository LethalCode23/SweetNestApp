package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Tipo con hoteles, unidad con reservas o usuario con reservas: no se puede borrar. */
public class DependentRecordsExistException extends BusinessRuleException {
    public DependentRecordsExistException(String message) {
        super(message, ApiResponseCode.DEPENDENT_RECORDS_EXIST, HttpStatus.CONFLICT);
    }
}
