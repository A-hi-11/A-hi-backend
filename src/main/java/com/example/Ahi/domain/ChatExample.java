package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChatExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long example_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    private String message;
    private boolean question_or_answer;
    private Long chat_order;
}
