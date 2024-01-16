package com.example.Ahi.service;

import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Preference;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.exception.AhiException;
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

import static com.example.Ahi.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final PromptRepository promptRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public String checkPassword(String memberId, String password){

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);

        if(!member.get().getPassword().equals(password))
            throw new AhiException(INVALID_PASSWORD);
//        if (!passwordEncoder.matches(password, member.get().getPassword()))
//            throw new AhiException(INVALID_PASSWORD);

        return "비밀번호가 일치합니다. 회원정보 수정이 가능합니다.";
    }

    public String updatePassword(String memberId,String newPassword){

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);
        if(member.get().getPassword().equals(newPassword))
            throw new AhiException(SAME_PASSWORD);

        member.get().setPassword(newPassword);
        //member.get().setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member.get());

        return "비밀번호가 변경되었습니다.";
    }

    public String updateProfileImg(String memberId, MultipartFile newImage){

        String imgUrl = "";
        try{
            imgUrl = s3Service.uploadProfileImage(newImage.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image",e);
        }

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);

        member.get().setProfileImage(imgUrl);
        memberRepository.save(member.get());

        return member.get().getProfileImage();
    }

    public String updateNickname(String memberId, String newNickname){

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);

        member.get().setNickname(newNickname);
        memberRepository.save(member.get());

        return member.get().getNickname();
    }

    public ArrayList<PromptListResponseDto> getLikedPrompt(String memberId) {

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);

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

    }



}
