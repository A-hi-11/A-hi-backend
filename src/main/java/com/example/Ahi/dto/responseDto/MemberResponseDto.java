package com.example.Ahi.dto.responseDto;

import com.example.Ahi.domain.Member;
import lombok.Data;

@Data
public class MemberResponseDto {
    private String member_id;
    private String nickname;
    private String profile_image;
    private String jwt;
    public MemberResponseDto(Member member, String jwt){
        this.member_id = member.getMember_id();
        this.nickname = member.getNickname();
        this.profile_image = member.getProfile_image();
        this.jwt = jwt;

    }
}
