package com.example.Ahi.dto.requestDto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MemberRequest {
    private String member_id; //이메일
    private String password;
    private String nickname;
    private String profile_image;
}
