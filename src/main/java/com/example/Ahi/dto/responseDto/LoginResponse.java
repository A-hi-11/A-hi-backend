package com.example.Ahi.dto.responseDto;

import lombok.Data;

@Data
public class LoginResponse {
    private String memberId;
    private String nickname;
    private String profileImg;
    private String jwt;
}
