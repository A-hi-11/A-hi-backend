package com.example.Ahi.dto.requestDto;

import com.amazonaws.util.StringUtils;
import io.jsonwebtoken.lang.Assert;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreferenceRequestDto {
    private String member_id;
    private Long prompt_id;
    private String status;

    public void validate() throws IllegalArgumentException {
        Assert.isTrue(!StringUtils.isNullOrEmpty(member_id));
        Assert.notNull(prompt_id);
        if (!"like".equals(status) && !"dislike".equals(status)) {
            throw new IllegalArgumentException();
        }
    }
}
