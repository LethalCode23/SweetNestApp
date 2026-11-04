package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** checkIn en el pasado. */
public class ReservationInPastException extends BusinessRuleException {
    public ReservationInPastException(String message) {
        super(message, ApiResponseCode.RESERVATION_IN_PAST, HttpStatus.BAD_REQUEST);
    }
}
