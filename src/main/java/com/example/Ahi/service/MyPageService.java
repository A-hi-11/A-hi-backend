package com.example.Ahi.service;

import com.example.Ahi.domain.Member;
import com.example.Ahi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    public String updatePassword(String newPassword){

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setPassword(newPassword);
            memberRepository.save(pmember);
            return "비밀번호가 변경되었습니다.";
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }

    public String updateProfileImg(String newImage){

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setProfile_image(newImage);
            memberRepository.save(pmember);
            return "프로필 이미지가 변경되었습니다.";
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }

    public String updateNickname(String newNickname){

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setNickname(newNickname);
            memberRepository.save(pmember);
            return "별명이 변경되었습니다.";
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }



}
