package com.example.Ahi.dto.requestDto;


import com.amazonaws.util.StringUtils;
import com.example.Ahi.entity.GptConfigInfo;
import io.jsonwebtoken.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatgptRequest {
    private String prompt;
    private GptConfigInfo gptConfigInfo;
    public void validate() throws IllegalArgumentException {
        Assert.isTrue(!StringUtils.isNullOrEmpty(prompt));
    }
}
