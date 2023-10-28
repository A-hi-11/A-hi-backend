package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private String content;
    private LocalDateTime Create_id;
}
