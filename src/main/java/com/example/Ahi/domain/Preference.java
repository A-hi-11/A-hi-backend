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
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preference_id;
    @ManyToOne
    @JoinColumn(name="prompt_id")
    private Prompt prompt;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;
    private String status;
}
