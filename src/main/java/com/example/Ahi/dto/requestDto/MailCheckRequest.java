package com.example.Ahi.dto.requestDto;

import lombok.Data;

@Data
public class MailCheckRequest {
    private String email;
    private int code;
}
