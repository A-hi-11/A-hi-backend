package com.example.Ahi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PromptListResponseDto {
    private String member_id;
    private String title;
    private String description;
    private String mediaType;
    private String category;
    private LocalDateTime create_time;
    private LocalDateTime update_time;

    // TODO: 추후 추가 예정
    private Long likes;
    private Long comments;
}
