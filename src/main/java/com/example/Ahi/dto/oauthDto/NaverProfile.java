package com.example.Ahi.dto.oauthDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverProfile {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    public class Response{
        String id;
        String nickname;
        String profile_image;
        String email;
        String name;
    }
}
