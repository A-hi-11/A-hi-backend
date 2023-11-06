package com.example.Ahi.dto;

import com.example.Ahi.domain.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PromptResponseDto {
    private Member member_id;
    private String title;
    private String description;
    private String mediaType;
    private String category;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
