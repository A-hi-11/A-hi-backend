package com.example.Ahi.dto.requestDto;


import com.amazonaws.util.StringUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentRequest {
    private String comment;
    public void validate() throws IllegalArgumentException {
        Assert.isTrue(!StringUtils.isNullOrEmpty(comment));

    }
}
