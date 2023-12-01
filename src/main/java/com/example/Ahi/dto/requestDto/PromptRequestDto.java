package com.example.Ahi.dto.requestDto;

import com.amazonaws.util.StringUtils;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.entity.GptConfigInfo;
import com.example.Ahi.entity.Message;
import io.jsonwebtoken.lang.Assert;
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

    public void validate() throws IllegalArgumentException {
        if (!"text".equals(mediaType) && !"image".equals(mediaType)) {
            throw new IllegalArgumentException();
        }
        if("text".equals(mediaType)){
            Assert.notNull(gptConfigInfo);
        }

        Assert.notEmpty(example);
        Assert.isTrue(!StringUtils.isNullOrEmpty(member_id));
        Assert.isTrue(!StringUtils.isNullOrEmpty(title));
        Assert.isTrue(!StringUtils.isNullOrEmpty(description));
        Assert.isTrue(!StringUtils.isNullOrEmpty(content));
        Assert.isTrue(!StringUtils.isNullOrEmpty(category));
        Assert.isTrue(!StringUtils.isNullOrEmpty(welcome_message));
    }

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
