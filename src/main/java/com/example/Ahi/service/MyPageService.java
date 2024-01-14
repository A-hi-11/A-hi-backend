package com.example.Ahi.service;

import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Preference;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.repository.CommentRepository;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.repository.PreferenceRepository;
import com.example.Ahi.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final PromptRepository promptRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    private final S3Service s3Service;


    public String updatePassword(String memberId,String newPassword){

        Optional<Member> member = memberRepository.findById(memberId);

        //TODO:소셜로그인 한 경우 처리
        if (member.isPresent()) {
            Member pmember = member.get();

            //String encoded = passwordEncoder.encode(newPassword);
            //System.out.println(encoded);

            pmember.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(pmember);

            return "비밀번호가 변경되었습니다.";
        } else {
            return "회원정보가 존재하지 않습니다.";
        }

    }

    public String checkPassword(String memberId, String curPassword){

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isPresent()) {
            Member pmember = member.get();

            //TODO: 나중에 저장 자체를 encoded된 비밀번호로 바꿔야함
            //String enCoded = passwordEncoder.encode(pmember.getPassword());
            //System.out.println(enCoded);
            Boolean isMatches = passwordEncoder.matches(curPassword, pmember.getPassword());
//            Boolean isMatches = curPassword.equals(pmember.getPassword());
            if(isMatches) { return "비밀번호가 일치합니다! 회원정보 수정이 가능합니다.";}
            else { return "비밀번호가 일치하지 않습니다. 회원정보 수정이 불가능합니다.";}
        }
        else { return "회원정보가 존재하지 않습니다.";}
    }

    public String updateProfileImg(String memberId, MultipartFile newImage){

        String imgUrl = "";
        try{
            imgUrl = s3Service.uploadProfileImage(newImage.getBytes());
            System.out.println(imgUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image",e);
        }

        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setProfileImage(imgUrl);
            memberRepository.save(pmember);
            return pmember.getProfileImage();
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }

    public String updateNickname(String memberId, String newNickname){

        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent()) {
            Member pmember = member.get();
            pmember.setNickname(newNickname);
            memberRepository.save(pmember);
            return pmember.getNickname();
        } else {
            return "해당 이메일을 가진 회원이 존재하지 않습니다.";
        }

    }

    public ArrayList<PromptListResponseDto> getLikedPrompt(String memberId) {

        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent()) {

            List<Preference> myPreferences = preferenceRepository.findByMemberAndStatus(member.get(), "like");
            ArrayList<Prompt> likedPromptList = new ArrayList<>();
            ArrayList<PromptListResponseDto> responses = new ArrayList<>();

            for (Preference preference : myPreferences) {
                Long promptId = preference.getPrompt().getPrompt_id();
                Prompt prompt = promptRepository.findById(promptId).get();
                likedPromptList.add(prompt);
            }

            for (Prompt prompt : likedPromptList) {

                List<Preference> likes = preferenceRepository.findByPromptAndStatus(prompt, "like");
                List<Preference> dislikes = preferenceRepository.findByPromptAndStatus(prompt, "dislike");
                List<Comment> commentList = commentRepository.findByPromptId(prompt);

                PromptListResponseDto responseDto = prompt
                        .toPromptListResponseDto(commentList.size(), likes.size(), dislikes.size());
                responses.add(responseDto);

            }

            return responses;

        } else {
            return null;
        }
    }



}
