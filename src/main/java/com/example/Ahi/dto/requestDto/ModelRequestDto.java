package com.example.Ahi.dto.requestDto;

import lombok.Data;

@Data
public class ModelRequestDto {
    private String member_id;
    private String prompt;
    private String model_type; // text or image
    private String negative;
    private long chat_room_id; // 없을 시 -1

    public void validate() throws IllegalArgumentException {
        if (!"text".equals(model_type) && !"image".equals(model_type)) {
            throw new IllegalArgumentException();
        }
    }
}
