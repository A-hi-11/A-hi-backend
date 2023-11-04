package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preference_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt_id;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member_id;
    private String status;
}
