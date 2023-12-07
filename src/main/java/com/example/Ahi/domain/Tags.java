package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Tag_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prompt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prompt prompt;
    private String content;
}
