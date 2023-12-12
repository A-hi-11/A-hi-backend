package com.example.Ahi.dto.responseDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PromptListResponseDto {
    private Long prompt_id;
    private String member_id;
    private String nickname;
    private String title;
    private String description;
    private String mediaType;
    private String category;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
    private Long likes;
    private long dislikes;
    private Long comments;
    private String image;
}
