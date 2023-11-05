package com.example.Ahi.entity;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private String message;
    private boolean isQuestion;
    private Long chat_order;

    public ChatExample toChatExample(Prompt prompt){
        return ChatExample.builder()
                .message(message)
                .isQuestion(isQuestion)
                .chat_order(chat_order)
                .prompt_id(prompt)
                .build();
    }
}
