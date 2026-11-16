package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** HU #28: el usuario no tiene una estadia terminada en el hotel, no puede valorarlo. */
public class ReviewNotAllowedException extends BusinessRuleException {
    public ReviewNotAllowedException(String message) {
        super(message, ApiResponseCode.REVIEW_NOT_ALLOWED, HttpStatus.FORBIDDEN);
    }
}
