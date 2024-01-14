package com.example.Ahi.dto.responseDto;

import com.example.Ahi.domain.Member;
import lombok.Data;

@Data
public class MemberResponseDto {
    private String member_id;
    private String nickname;
    private String profile_image;
    private Boolean isOAuth;
    private String jwt;
    public MemberResponseDto(Member member, String jwt){
        this.member_id = member.getMemberId();
        this.nickname = member.getNickname();
        this.profile_image = member.getProfileImage();
        this.isOAuth = member.getIsOAuth();
        this.jwt = jwt;

    }
}
