package com.example.processing.exception.handler;

import com.example.processing.exception.HipsumClientException;
import com.example.processing.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode("VALIDATION_ERROR")
                        .errorMessage(msg)
                        .build());
    }

    @ExceptionHandler(HipsumClientException.class)
    public ResponseEntity<ErrorResponse> handleHipsum(HipsumClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .errorCode("UPSTREAM_TIMEOUT")
                        .errorMessage("Hipsum did not respond in time")
                        .build());
    }


    private String formatViolation(ConstraintViolation<?> v) {
        return v.getPropertyPath() + " " + v.getMessage();
    }
}
