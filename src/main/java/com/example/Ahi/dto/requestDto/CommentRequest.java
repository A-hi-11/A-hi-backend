package com.example.Ahi.dto.requestDto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentRequest {
    private String comment;
}
