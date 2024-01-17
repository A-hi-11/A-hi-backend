package com.example.Ahi.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class ChatStreamResponseDto implements Serializable {
    private String id;
    private String object;
    private LocalDate created;
    private String model;
    @JsonProperty("system_fingerprint")
    private String system_fingerprint;
    private List<ChatStreamResponseDto.Choice> choices;

    @Data
    public static class Choice{
        private ChatStreamResponseDto.Choice.Delta delta;
        private Integer index;
        @JsonProperty("finish_reason")
        private String finishReason;
        private String logprobs;

        @Data
        public static class Delta{
            private String content;
        }
    }
}
