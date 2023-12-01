package com.example.Ahi.dto.requestDto;

import com.amazonaws.util.StringUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.Data;

@Data
public class ModelRequestDto {
    private String member_id;  // JWT 사용 시 없어도 됌
    private String prompt;
    private String model_type; // text or image
    private long chat_room_id; // 없을 시 -1

    public void validate() throws IllegalArgumentException {
        Assert.isTrue(!StringUtils.isNullOrEmpty(member_id));
        if (!"text".equals(model_type) && !"image".equals(model_type)) {
            throw new IllegalArgumentException();
        }
    }
}
