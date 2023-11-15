package com.example.Ahi.domain;

import com.example.Ahi.entity.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ChatExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long example_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;
    private String message;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isQuestion;
    private Long chat_order;

    public Message toMessage(){
        return Message.builder()
                .isQuestion(isQuestion)
                .message(message)
                .chat_order(chat_order)
                .build();

    }
}
