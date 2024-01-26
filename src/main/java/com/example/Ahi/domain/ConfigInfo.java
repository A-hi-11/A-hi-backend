package com.example.Ahi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ConfigInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ConfigInfoId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prompt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prompt promptId;
    private String modelName;
    private double temperature;
    private int maximumLength;
    private String stopSequence;
    private double topP;
    private double frequencyPenalty;
    private double presencePenalty;
}
