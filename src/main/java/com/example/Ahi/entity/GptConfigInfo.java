package com.example.Ahi.entity;

import com.example.Ahi.domain.ConfigInfo;
import com.example.Ahi.domain.Prompt;
import lombok.Data;

@Data
public class GptConfigInfo {
    private String model_name;
    private double temperature;
    private int maximum_length;
    private String stop_sequence;
    private double top_p;
    private double frequency_penalty;
    private double presence_penalty;

    public ConfigInfo toConfigInfo(Prompt prompt){
        return ConfigInfo.builder()
                .promptId(prompt)
                .maximumLength(maximum_length)
                .temperature(temperature)
                .frequencyPenalty(frequency_penalty)
                .stopSequence(stop_sequence)
                .modelName(model_name)
                .topP(top_p)
                .presencePenalty(presence_penalty)
                .build();
    }
}
