package com.example.Ahi.exception;


import lombok.Getter;

@Getter
public class AhiException extends RuntimeException{

    private int result;
    private ErrorCode errorCode;
    private String message;

    public AhiException(ErrorCode errorCode) {
        this.result = errorCode.getStatus();
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}