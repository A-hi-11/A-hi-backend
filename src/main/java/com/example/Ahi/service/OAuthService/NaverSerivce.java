package com.example.Ahi.service.OAuthService;

import com.example.Ahi.domain.Member;
import com.example.Ahi.dto.oauthDto.NaverProfile;
import com.example.Ahi.dto.oauthDto.NaverToken;
import com.example.Ahi.repository.MemberRepository;
import com.example.Ahi.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.Ahi.service.MemberService.MemberPasswordGenerator.generateRandomPassword;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@RequiredArgsConstructor
public class NaverSerivce {
    private final MemberRepository memberRepository;

    @Value("${naver.clientId}")
    String nClientId;

    @Value("${naver.clientSecret}")
    String nClientSecret;

    @Value("${naver.redirectUri}")
    String nRedirectUri;

    @Value("${jwt-secret-key}")
    private String secretKey;
    private Long expiredMs = 1000*60*60L; //한시간


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

        String member_id = JwtUtil.extractMember(response,secretKey);

        Member member = memberRepository.findById(member_id).get();

        return "http://api.a-hi-prompt.com?member_id=" +
                member.getMemberId() +
                "&nickname=" + member.getNickname() +
                "&profile_image=" + member.getProfileImage() +
                "&isOAuth=" + member.getIsOAuth() +
                "&jwt=" + response;
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

        //String test = generateRandomPassword(5);

        //없는 멤버 회원정보 저장(회원가입)
        if(Opmember.isEmpty()) {
            member = Member.builder()
                    .memberId(profile.getResponse().getEmail())
                    .password(generateRandomPassword(8))
                    .nickname(profile.getResponse().getNickname())
                    .profileImage(profile.getResponse().getProfile_image())
                    .lastUpdateTime(LocalDateTime.now())
                    .isOAuth(true)
                    .build();

            memberRepository.save(member);
            //System.out.println(test);

            response = JwtUtil.createJwt(member.getMemberId(), secretKey, expiredMs);
        }
        //이미 회원정보가 존재하는 멤버
        else {
            response = JwtUtil.createJwt(Opmember.get().getMemberId(), secretKey, expiredMs);
        }
        return response;
    }
}
