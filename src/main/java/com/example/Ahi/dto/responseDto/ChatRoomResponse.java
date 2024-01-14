package com.example.Ahi.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private Long chat_room_id;
    private String chat_room_name;
    private LocalDateTime create_time;
    private String model_type;
    private String last_message;
}
