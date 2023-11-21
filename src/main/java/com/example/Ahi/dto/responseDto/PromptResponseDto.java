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
    private String nickname;
    private String description;
    private String content;
    private String mediaType;
    private String category;
    private boolean permission;
    private String welcome_message;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
    private ArrayList<ArrayList<Message>> example;
    private long likes;
    private long dislikes;
    private ArrayList<CommentListResponse> comments;
    private Set<String> tags;
}
