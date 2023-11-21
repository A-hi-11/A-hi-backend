package com.example.Ahi.dto.requestDto;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.entity.GptConfigInfo;
import com.example.Ahi.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
public class PromptRequestDto {
    private String member_id;
    private String title;
    private String description;
    private String content;
    private String mediaType;
    private String category;
    private boolean permission;
    private String welcome_message;
    private GptConfigInfo gptConfigInfo;

    private ArrayList<ArrayList<Message>> example;
    private Set<String> tags;

    // 예시 사용 내역 -> List로 받고 각각을 chatExample로 만든 후 각자 저장 + 두 example은 형식 통합
    // tag -> list로 받고 각각을 tags로 만든 후 각각 저장

    public Prompt toPrompt(Member member, LocalDateTime now){
        return Prompt.builder()
                .member(member)
                .title(title)
                .description(description)
                .content(content)
                .mediaType(mediaType)
                .category(category)
                .permission(permission)
                .welcome_message(welcome_message)
                .create_time(now)
                .update_time(now)
                .build();
    }

}
