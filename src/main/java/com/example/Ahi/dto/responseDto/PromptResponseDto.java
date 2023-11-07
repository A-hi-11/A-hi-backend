package com.example.Ahi.dto.responseDto;

import com.example.Ahi.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PromptResponseDto {
    private Long prompt_id;
    private String member_id;
    private String title;
    private String description;
    private String content;
    private String mediaType;
    private String category;
    private boolean permission;
    private String welcome_message;
    private LocalDateTime create_time;
    private LocalDateTime update_time;

    // TODO: 좋아요수, 댓글 목록 추가 필요
    private ArrayList<ArrayList<Message>> example;
    private Set<String> tags;
}
