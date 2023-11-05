package com.example.Ahi.domain;


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
}
