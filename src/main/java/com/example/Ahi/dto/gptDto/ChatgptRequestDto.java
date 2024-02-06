package com.example.Ahi.dto.gptDto;

import com.example.Ahi.dto.requestDto.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatgptRequestDto implements Serializable {
    private List<Message> messages;
    private String model;
    private Double temperature;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @JsonProperty("stop")
    private String stop_sequences;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("frequency_penalty")
    private Double frequency_penalty;
    @JsonProperty("presence_penalty")
    private Double presence_penalty;
    @JsonProperty("stream")
    private boolean stream ;
}
