package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Reserva inexistente. */
public class ReservationNotFoundException extends BusinessRuleException {
    public ReservationNotFoundException(String message) {
        super(message, ApiResponseCode.RESERVATION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
