package com.example.Ahi.domain;

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
    private Prompt prompt_id;
    private String message;
    private boolean isQuestion;
}
