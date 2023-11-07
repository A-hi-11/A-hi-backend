package com.example.Ahi.entity;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Message {
    private String message;
    private boolean isQuestion;
    private Long chat_order;

    public ChatExample toChatExample(Prompt prompt){
        return ChatExample.builder()
                .message(message)
                .isQuestion(isQuestion)
                .prompt(prompt)
                .chat_order(chat_order)
                .build();
    }
}
