package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Tag_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    private String content;
}
