package com.example.Ahi.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class KoauthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;
}