package com.dh.demo.exception;

import com.dh.demo.common.ApiResponseCode;
import com.dh.demo.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {

        String errs = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildError("Datos inválidos: " + errs, HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return buildError("Credenciales inválidas", HttpStatus.UNAUTHORIZED, ApiResponseCode.INVALID_CREDENTIALS);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UsernameNotFoundException ex) {
        return buildError("Usuario no encontrado", HttpStatus.NOT_FOUND, ApiResponseCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {

        log.error("Error creating user", ex);
        return buildError("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR, ApiResponseCode.INTERNAL_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(String message, HttpStatus status, ApiResponseCode code) {
        ApiResponse<Void> body = ApiResponse.error(message, code);
        return ResponseEntity.status(status).body(body);
    }
}