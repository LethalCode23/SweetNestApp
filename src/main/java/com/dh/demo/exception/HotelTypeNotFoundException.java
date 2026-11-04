package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Tipo de alojamiento inexistente. */
public class HotelTypeNotFoundException extends BusinessRuleException {
    public HotelTypeNotFoundException(String message) {
        super(message, ApiResponseCode.HOTEL_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
