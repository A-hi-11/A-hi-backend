package com.example.Ahi.domain;


import com.example.Ahi.dto.responseDto.PromptListResponseDto;
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
    private Member member;
    private String title;
    private String description;
    @Column(length = 10000)
    private String content;
    private String mediaType;
    private String category;
    private boolean permission;
    @Column(length = 65535)
    private String welcome_message;
    @Column(name = "create_time")
    private LocalDateTime create_time;
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public PromptListResponseDto toPromptListResponseDto(long comments, long likes, long dislikes){
        return PromptListResponseDto.builder()
                .prompt_id(prompt_id)
                .member_id(member.getMember_id())
                .nickname(member.getNickname())
                .title(title)
                .description(description)
                .mediaType(mediaType)
                .category(category)
                .create_time(create_time)
                .update_time(updateTime)
                .comments(comments)
                .likes(likes)
                .dislikes(dislikes)
                .build();
    }

    public PromptResponseDto toPromptResponseDto(){
        return PromptResponseDto.builder()
                .title(title)
                .description(description)
                .mediaType(mediaType)
                .category(category)
                .permission(permission)
                .update_time(updateTime)
                .create_time(create_time)
                .prompt_id(prompt_id)
                .content(content)
                .member_id(member.getMember_id())
                .welcome_message(welcome_message)
                .build();
    }
}
