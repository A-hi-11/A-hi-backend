package com.example.Ahi.dto.responseDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikedPromptListResponse {
    private Long prompt_id;
    private String title;
    private String content;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
