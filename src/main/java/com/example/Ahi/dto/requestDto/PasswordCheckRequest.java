package com.example.Ahi.dto.requestDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordCheckRequest {
    private String cur_password;
}
