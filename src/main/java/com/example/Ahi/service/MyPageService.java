package com.example.Ahi.service;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Preference;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.LikedPromptResponse;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PreferenceRepository;
import com.example.Ahi.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final PromptRepository promptRepository;

    private final PasswordEncoder passwordEncoder;

    public String updatePassword(String newPassword){

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        //TODO:예외처리(이전 비밀번호와 같은지, 비밀조건 조건 만족하는지) ,,, 자체로그인이 아니면 비밀번호는 null이긴 함
        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setPassword(newPassword);
            memberRepository.save(pmember);
            return "비밀번호가 변경되었습니다.";
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }

    public String checkPassword(String curPassword){

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        if(member.isPresent()) {
            Member pmember = member.get();

            //TODO: 나중에 저장 자체를 encoded된 비밀번호로 바꿔야함
            //String enCoded = passwordEncoder.encode(pmember.getPassword());
            //System.out.println(enCoded);
            Boolean isMatches = passwordEncoder.matches(curPassword, pmember.getPassword());

            if(isMatches) { return "비밀번호가 일치합니다! 회원정보 수정이 가능합니다.";}
            else { return "비밀번호가 일치하지 않습니다. 회원정보 수정이 불가능합니다.";}
        }
        else { return "회원정보가 존재하지 않습니다.";}
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

    public ArrayList<LikedPromptResponse> getLikedPrompt() {

        Optional<Member> member = memberRepository.findById("test@gmail.com");

        if (member.isPresent()) {
            List<Preference> myPreferences = preferenceRepository.findByMemberAndStatus(member.get(), "like");
            ArrayList<Prompt> likedPromptList = new ArrayList<>();

            for (Preference preference : myPreferences) {
                Long promptId = preference.getPrompt().getPrompt_id();
                Prompt prompt = promptRepository.findById(promptId).get();
                likedPromptList.add(prompt);
            }

            ArrayList<LikedPromptResponse> responses = new ArrayList<>();

            for (Prompt prompt : likedPromptList) {
                LikedPromptResponse response = new LikedPromptResponse();
                response.setPrompt_id(prompt.getPrompt_id());
                response.setContent(prompt.getContent());
                response.setTitle(prompt.getTitle());
                response.setCreate_time(prompt.getCreate_time());
                response.setUpdate_time(prompt.getUpdate_time());

                responses.add(response);
            }

            return responses;

        } else {
            return null;
        }
    }



}
