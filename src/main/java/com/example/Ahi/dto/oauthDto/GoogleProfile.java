package com.example.Ahi.dto.oauthDto;

import com.example.Ahi.domain.Member;
import lombok.Data;

import java.time.LocalDateTime;

import static com.example.Ahi.service.MemberService.MemberPasswordGenerator.generateRandomPassword;

@Data
public class GoogleProfile {
    public String sub;
    public String email;
    public Boolean email_verified;
    public String name;
    public String given_name;
    public String family_name;
    public String picture;
    public String locale;
    public String hd;
    public Member toMember(){
        return Member.builder()
                .memberId(email)
                .profileImage(picture)
                .password(generateRandomPassword(8))
                .lastUpdateTime(LocalDateTime.now())
                .nickname(name)
                .build();
    }
}
