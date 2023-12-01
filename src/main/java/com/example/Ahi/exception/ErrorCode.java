package com.example.Ahi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(400,HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATED_USER_NAME(400,HttpStatus.CONFLICT, "사용자가 중복됩니다"),
    INVALID_PERMISSION(401,HttpStatus.UNAUTHORIZED, "권한이 없습니다"),
    NOT_FOUND(400,HttpStatus.NOT_FOUND, "존재하지 않는 정보를 요청하였습니다."),
    TIMEOUT_ERROR(500,HttpStatus.INTERNAL_SERVER_ERROR, "서버 요청에 실패하였습니다."),
    GPT_TOKEN_ERROR(500,HttpStatus.INTERNAL_SERVER_ERROR, "제한된 토큰 수를 초과하였습니다."),
    DATABASE_ERROR(500,HttpStatus.INTERNAL_SERVER_ERROR, "DB에러가 발생하였습니다");


    private int status;
    private HttpStatus error;
    private String message;
}
