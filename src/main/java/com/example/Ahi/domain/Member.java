package com.example.Ahi.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Member {
    @Id
    private String member_id;
    private String password;
    private String nickname;
    private LocalDateTime last_update_time;
    private String profile_image;

    public Member(){

    }
}
