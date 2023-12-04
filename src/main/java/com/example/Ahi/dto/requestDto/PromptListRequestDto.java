package com.example.Ahi.dto.requestDto;

import lombok.Data;

@Data
public class PromptListRequestDto {
    private String sort;
    private String category;
    private String search;
    private String mediaType;

    public void validate() throws IllegalArgumentException {
        if (!"text".equals(mediaType) && !"image".equals(mediaType)) {
            throw new IllegalArgumentException();
        }
    }
}
