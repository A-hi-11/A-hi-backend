package com.example.Ahi.dto.requestDto;


import com.example.Ahi.entity.GptConfigInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatgptRequest {
    private String prompt;
    private GptConfigInfo gptConfigInfo;
}
