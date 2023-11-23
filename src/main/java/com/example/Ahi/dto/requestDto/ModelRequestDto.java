package com.example.Ahi.dto.requestDto;

import lombok.Data;

@Data
public class ModelRequestDto {
    private String member_id;  // JWT 사용 시 없어도 됌
    private String prompt;
    private String model_type; // text or image
    private long chat_room_id; // 없을 시 -1

}
