package com.example.Ahi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404,HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"),
    PROMPT_NOT_FOUND(404,HttpStatus.NOT_FOUND, "프롬프트를 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(404,HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    CHATROOM_NOT_FOUND(404,HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    INVALID_PERMISSION(401,HttpStatus.UNAUTHORIZED, "권한이 없습니다"),
    DUPLICATED_USER(409,HttpStatus.CONFLICT, "이미 존재하는 회원입니다"),
    FAIL_TO_SEND(500,HttpStatus.INTERNAL_SERVER_ERROR,"전송에 실패하였습니다."),
    INVALID_PASSWORD(403, HttpStatus.FORBIDDEN,"잘못된 비밀번호입니다."),
    SAME_PASSWORD(409,HttpStatus.CONFLICT, "기존 비밀번호와 입력 비밀번호가 같습니다."),
    DUPLICATED_NICKNAME(409,HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다."),
    DATABASE_ERROR(400,HttpStatus.INTERNAL_SERVER_ERROR, "DB에러가 발생하였습니다");

    private int status;
    private HttpStatus error;
    private String message;
}
