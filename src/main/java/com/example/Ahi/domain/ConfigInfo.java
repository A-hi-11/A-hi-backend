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
    private Long Config_info_id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prompt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prompt promptId;
    private String model_name;
    private float temperature;
    private Long maximum_length;
    private String stop_sequence;
    private float top_p;
    private float frequency_penalty;
    private float presence_penalty;
}
