package com.example.Ahi.dto.requestDto;

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
    private String model = "gpt-3.5-turbo";
    private Double temperature=1.0;
    @JsonProperty("max_tokens")
    private Integer maxTokens=100;
    @JsonProperty("stop")
    private String stop_sequences="\n";
    @JsonProperty("top_p")
    private Double topP=1.0;
    @JsonProperty("frequency_penalty")
    private Double frequency_penalty=0.0;
    @JsonProperty("presence_penalty")
    private Double presence_penalty=0.0;
    @JsonProperty("stream")
    private boolean stream = true;
}
