package com.example.Ahi.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String memberId;
    private String nickname;
    private String profileImg;
    private String jwt;
}
