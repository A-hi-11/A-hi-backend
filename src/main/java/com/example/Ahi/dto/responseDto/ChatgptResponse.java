package com.example.Ahi.dto.responseDto;

import lombok.Data;


@Data
public class ChatgptResponse {
    private String answer;
    private Long chat_room_id;
}
