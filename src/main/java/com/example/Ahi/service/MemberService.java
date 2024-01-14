package com.example.Ahi.service;


import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.requestDto.MemberRequest;
import com.example.Ahi.dto.responseDto.LoginResponse;
import com.example.Ahi.exception.ErrorCode;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.utils.JwtUtil;
import com.example.Ahi.exception.AhiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.Ahi.exception.ErrorCode.INVALID_PASSWORD;
import static com.example.Ahi.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${jwt-secret-key}")
    private String secretKey;
    private Long expiredMs = 1000*60*60L; //한시간
    private final MemberRepository memberRepository;

    public String signup(MemberRequest request){
        Optional<Member> member = memberRepository.findById(request.getMember_id());
        String response = "";

        if(member.isPresent())
            throw new AhiException(ErrorCode.DUPLICATED_USER);

        Member newMember = Member.builder()
                .memberId(request.getMember_id())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .profileImage(request.getProfile_image())
                .lastUpdateTime(LocalDateTime.now())
                .build();

        memberRepository.save(newMember);
        response = "회원가입에 성공하였습니다.";

        return response;
    }



    public LoginResponse loginAndReturnToken(String memberId, String password){
        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty())
            throw new AhiException(USER_NOT_FOUND);
        if (!member.get().getPassword().equals(password))
            throw new AhiException(INVALID_PASSWORD);

        LoginResponse response = LoginResponse.builder()
                .memberId(member.get().getMemberId())
                .nickname(member.get().getNickname())
                .profileImg(member.get().getProfileImage())
                .jwt(JwtUtil.createJwt(memberId,secretKey,expiredMs))
                .build();

        return response;
    }


    public class MemberPasswordGenerator {

        private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        private static final String NUMBER = "0123456789";
        private static final String SPECIAL_CHARS = "!@#$%^&*()_-+=<>?";

        private static final String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;

        public static String generateRandomPassword(int length) {
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < length; i++) {
                int index = random.nextInt(PASSWORD_CHARS.length());
                password.append(PASSWORD_CHARS.charAt(index));
            }

            return password.toString();
        }

    }
}
