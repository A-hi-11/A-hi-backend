package com.example.Ahi.entity;

import com.example.Ahi.domain.ConfigInfo;
import com.example.Ahi.domain.Prompt;
import lombok.Data;

@Data
public class GptConfigInfo {
    private String model_name;
    private float temperature;
    private Long maximum_length;
    private String stop_sequence;
    private float top_p;
    private float frequency_penalty;
    private float presence_penalty;

    public ConfigInfo toConfigInfo(Prompt prompt){
        return ConfigInfo.builder()
                .promptId(prompt)
                .maximum_length(maximum_length)
                .temperature(temperature)
                .frequency_penalty(frequency_penalty)
                .stop_sequence(stop_sequence)
                .model_name(model_name)
                .top_p(top_p)
                .presence_penalty(presence_penalty)
                .build();
    }
}
