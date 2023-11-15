package com.example.Ahi.dto.requestDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreferenceRequestDto {
    private String member_id;
    private Long prompt_id;
    private String status;
}
