package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import org.springframework.http.HttpStatus;

/** El usuario no esta marcado como anfitrion (users.userHost != 'S'). */
public class UserNotHostException extends BusinessRuleException {
    public UserNotHostException(String message) {
        super(message, ApiResponseCode.USER_NOT_HOST, HttpStatus.CONFLICT);
    }
}
