package com.example.Ahi.dto.oauthDto;

import com.example.Ahi.domain.Member;
import lombok.Data;

import java.time.LocalDateTime;

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
                .member_id(email)
                .profile_image(picture)
                .password("google")
                .last_update_time(LocalDateTime.now())
                .nickname(name)
                .build();
    }
}
