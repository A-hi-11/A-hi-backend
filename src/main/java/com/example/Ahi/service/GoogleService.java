package com.example.Ahi.service;

import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.oauthDto.GoogleProfile;
import com.example.Ahi.dto.oauthDto.GoogleToken;
import com.example.Ahi.dto.responseDto.MemberResponseDto;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
@Service
@RequiredArgsConstructor
public class GoogleService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String gClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String gClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String gRedirectUrl;
    @Value("${jwt-secret-key}")
    private String secretKey;

    private final MemberRepository memberRepository;

    public String getGoogleAccessToken(String accessCode) {
        GoogleToken googleToken = getGoogleToken(accessCode);
        GoogleProfile googleProfile = getProfile(googleToken.getAccess_token());
        Member member = memberRepository.findById(googleProfile.getEmail()).orElse(null);

        if(member == null){
            member = googleProfile.toMember();
            memberRepository.save(member);
        }
        // jwt 생성 후 리디렉션 uri 와 함께 리턴
        Long expiredMs = 1000 * 60 * 60L;
        String jwt = JwtUtil.createJwt(member.getMember_id(), secretKey, expiredMs);
        // TODO: 프론트 서버의 주소로 리디렉트하도록 구현 추가로 멤버 정보 넣기
        MemberResponseDto memberResponseDto = new MemberResponseDto(member, jwt);

        return "http://localhost:3000?member_id=" +
                memberResponseDto.getMember_id() +
                "&nickname=" + memberResponseDto.getNickname() +
                "&profile_image=" + memberResponseDto.getProfile_image() +
                "&jwt=" + memberResponseDto.getJwt();
    }

    private GoogleToken getGoogleToken(String accessCode){
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", accessCode);
        params.add("client_id", gClientId);
        params.add("client_secret", gClientSecret);
        params.add("redirect_uri",gRedirectUrl);
        params.add("grant_type", "authorization_code");


        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params,headers);

        ResponseEntity<GoogleToken> response = rt.postForEntity(
                "https://oauth2.googleapis.com/token",
                googleTokenRequest,
                GoogleToken.class
        );

        return response.getBody();
    }

    private GoogleProfile getProfile(String token){
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);

        HttpEntity<MultiValueMap<String, String>> googleProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> googleProfileResponse = rt.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.POST,
                googleProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleProfile googleProfile = new GoogleProfile();

        try {
            googleProfile = objectMapper.readValue(googleProfileResponse.getBody(), GoogleProfile.class);
        } catch (JsonProcessingException e) {
            System.out.println(googleProfile);
            e.printStackTrace();
        }
        return googleProfile;
    }
}
