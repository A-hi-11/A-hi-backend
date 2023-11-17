package com.example.Ahi.service;


import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.requestDto.MemberRequest;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${jwt-secret-key}")
    private String secretKey;
    private Long expiredMs = 1000*60*60L; //한시간
    private final MemberRepository memberRepository;

    public String signup(MemberRequest request){
        Optional<Member> member = memberRepository.findById(request.getMember_id());
        Member newMember = new Member();
        String response = "";

        if(member.isPresent()){
            response = "이미 존재하는 이메일입니다. 회원가입에 실패하였습니다.";
        }
        else{
            newMember.setMember_id(request.getMember_id());
            newMember.setPassword(request.getPassword());
            newMember.setNickname(request.getNickname());
            newMember.setProfile_image(request.getProfile_image());
            newMember.setLast_update_time(LocalDateTime.now());

            memberRepository.save(newMember);
            response = "회원가입에 성공하였습니다.";
        }

        return response;
    }



    public String login(String member_id, String password){
        //TODO: 일치하는 멤버 확인

        return JwtUtil.createJwt(member_id,secretKey,expiredMs);
    }
}
