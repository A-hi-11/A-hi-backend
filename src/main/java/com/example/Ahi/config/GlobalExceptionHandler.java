package com.example.Ahi.config;

import exception.AhiException;
import exception.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<Object> handleConflict(RuntimeException ex) {
        Map body = Map.of("status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AhiException.class)
    public ResponseEntity<?> ahiExceptionHandler(AhiException e) {
        e.printStackTrace();

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ExceptionDto(e.getErrorCode()));
    }
}
