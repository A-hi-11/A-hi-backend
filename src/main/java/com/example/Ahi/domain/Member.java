package com.example.Ahi.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Member {
    @Id
    private String memberId;
    private String password;
    private String nickname;
    private LocalDateTime lastUpdateTime;
    private String profileImage;
    private Boolean isOAuth;

    public Member(){

    }
}
