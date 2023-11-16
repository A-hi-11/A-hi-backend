package com.example.Ahi.dto.responseDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomResponse {
    private Long chat_room_id;
    private String chat_room_name;
    private LocalDateTime create_time;
    private String model_type;
    private String last_message;
}
