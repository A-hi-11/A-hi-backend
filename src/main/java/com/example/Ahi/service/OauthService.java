package com.example.Ahi.service;

import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.oauthDto.KakaoProfile;
import com.example.Ahi.dto.oauthDto.KakaoToken;
import com.example.Ahi.dto.oauthDto.NaverProfile;
import com.example.Ahi.dto.oauthDto.NaverToken;
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

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final MemberRepository memberRepository;
//
    @Value("${kakao.clientId}")
    String clientId;

    @Value("${kakao.redirectUrl}")
    String redirectUrl;
//
    @Value("${naver.clientId}")
    String nClientId;

    @Value("${naver.clientSecret}")
    String nClientSecret;

    @Value("${naver.redirectUri}")
    String nRedirectUri;
//
    @Value("${jwt-secret-key}")
    private String secretKey;
    private Long expiredMs = 1000*60*60L; //한시간
//

    public String kakaoLogin(String accessCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        //APPLICATION_FORM_URLENCODED "application/x-www-form-urlencoded;charset=utf-8"
        headers.add("Content-type", String.valueOf(APPLICATION_FORM_URLENCODED));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        //프론트 url +"/auth"
        params.add("redirect_uri", redirectUrl);
        params.add("code", accessCode);
        //client_secret

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken = new KakaoToken();
        try {
            kakaoToken = objectMapper.readValue(accessTokenResponse.getBody(), KakaoToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        KakaoProfile profile = findKakaoProfile(kakaoToken.getAccess_token());
        String response = saveKakaoMember(profile);

        return response;

    }

    public String saveKakaoMember(KakaoProfile profile){

        String response = "";

        Optional<Member> Opmember = memberRepository.findById("없는멤버");

        if(Opmember.isEmpty()) {
            Member member;
            member = Member.builder()
                    .member_id("tmpMail@kakao.com")
                    .password("KAKAO")
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .profile_image(profile.getKakao_account().getProfile().getProfile_image_url())
                    .build();

            memberRepository.save(member);
            response = JwtUtil.createJwt(member.getMember_id(), secretKey, expiredMs);
        }
        //이미 회원정보가 존재하는 멤버
        else {
            response = JwtUtil.createJwt(Opmember.get().getMember_id(), secretKey, expiredMs);
        }

        return response;
    }

    public KakaoProfile findKakaoProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        //System.out.println(kakaoProfileResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = new KakaoProfile();
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            //System.out.println(kakaoProfile);
            e.printStackTrace();
        }

        return kakaoProfile;
    }

    public String naverLogin(String accessCode) {

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        //APPLICATION_FORM_URLENCODED "application/x-www-form-urlencoded;charset=utf-8"
        headers.add("Content-type", String.valueOf(APPLICATION_FORM_URLENCODED));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", nClientId);
        params.add("client_secret", nClientSecret);
        params.add("code", accessCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        NaverToken naverToken = new NaverToken();
        try {
            naverToken = objectMapper.readValue(accessTokenResponse.getBody(), NaverToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        NaverProfile profile = findNaverProfile(naverToken.getAccessToken());
        String response = saveNaverMember(profile);

        return response;

    }

    public NaverProfile findNaverProfile(String token) {

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> naverProfileResponse = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );

        //System.out.println(naverProfileRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        NaverProfile naverProfile = new NaverProfile();
        try {
            naverProfile = objectMapper.readValue(naverProfileResponse.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            System.out.println(naverProfile);
            e.printStackTrace();
        }

        //System.out.println(naverProfile);
        return naverProfile;
    }

    public String saveNaverMember(NaverProfile profile){

        Optional<Member> Opmember = memberRepository.findById(profile.getResponse().getEmail());
        Member member;
        String response;

        //없는 멤버 회원정보 저장(회원가입)
        if(Opmember.isEmpty()) {
            member = Member.builder()
                    .member_id(profile.getResponse().getEmail())
                    .password("NAVER")
                    .nickname(profile.getResponse().getNickname())
                    .profile_image(profile.getResponse().getProfile_image())
                    .build();

            memberRepository.save(member);
            //System.out.println(member);

            response = JwtUtil.createJwt(member.getMember_id(), secretKey, expiredMs);
        }
        //이미 회원정보가 존재하는 멤버
        else {
            response = JwtUtil.createJwt(Opmember.get().getMember_id(), secretKey, expiredMs);
        }
        return response;
    }


}
