package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** Usuario inexistente (ej. hostId inexistente al asignar anfitrion). */
public class UserNotFoundException extends BusinessRuleException {
    public UserNotFoundException(String message) {
        super(message, ApiResponseCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
