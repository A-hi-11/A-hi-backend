package com.example.Ahi.domain;


import com.example.Ahi.dto.requestDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private String title;
    private String description;
    @Column(length = 10000)
    private String content;
    private String mediaType;
    private String category;
    private boolean permission;
    private String welcome_message;
    private LocalDateTime create_time;
    private LocalDateTime update_time;

    public PromptListResponseDto toPromptListResponseDto(){
        return PromptListResponseDto.builder()
                .member_id(member_id.getMember_id())
                .title(title)
                .description(description)
                .mediaType(mediaType)
                .category(category)
                .create_time(create_time)
                .update_time(update_time)
                .build();
    }

    public PromptResponseDto toPromptResponseDto(){
        return PromptResponseDto.builder()
                .title(title)
                .description(description)
                .mediaType(mediaType)
                .category(category)
                .permission(permission)
                .update_time(update_time)
                .create_time(create_time)
                .prompt_id(prompt_id)
                .content(content)
                .member_id(member_id.getMember_id())
                .welcome_message(welcome_message)
                .build();
    }
}
