//package com.example.Ahi.service.OAuthService;
//
//import com.example.Ahi.domain.Member;
//import com.example.Ahi.dto.oauthDto.KakaoProfile;
//import com.example.Ahi.dto.oauthDto.KakaoToken;
//import com.example.Ahi.repository.MemberRepository;
//import com.example.Ahi.utils.JwtUtil;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Optional;
//
//import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoService {
//    private final MemberRepository memberRepository;
//
//    @Value("${kakao.clientId}")
//    String clientId;
//
//    @Value("${kakao.redirectUrl}")
//    String redirectUrl;
//
//    @Value("${jwt-secret-key}")
//    private String secretKey;
//    private Long expiredMs = 1000*60*60L; //한시간
//
//    public String kakaoLogin(String accessCode) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//
//        //APPLICATION_FORM_URLENCODED "application/x-www-form-urlencoded;charset=utf-8"
//        headers.add("Content-type", String.valueOf(APPLICATION_FORM_URLENCODED));
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        //프론트 url +"/auth"
//        params.add("redirect_uri", redirectUrl);
//        params.add("code", accessCode);
//        //client_secret
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        KakaoToken kakaoToken = new KakaoToken();
//        try {
//            kakaoToken = objectMapper.readValue(accessTokenResponse.getBody(), KakaoToken.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        KakaoProfile profile = findKakaoProfile(kakaoToken.getAccess_token());
//        String response = saveKakaoMember(profile);
//
//        return response;
//
//    }
//
//    public String saveKakaoMember(KakaoProfile profile){
//
//        String response = "";
//
//        Optional<Member> Opmember = memberRepository.findById("없는멤버");
//
//        if(Opmember.isEmpty()) {
//            Member member;
//            member = Member.builder()
//                    .memberId("tmpMail@kakao.com")
//                    .password("KAKAO")
//                    .nickname(profile.getKakao_account().getProfile().getNickname())
//                    .profileImage(profile.getKakao_account().getProfile().getProfile_image_url())
//                    .isOAuth(true)
//                    .build();
//
//            memberRepository.save(member);
//            response = JwtUtil.createJwt(member.getMemberId(), secretKey, expiredMs);
//        }
//        //이미 회원정보가 존재하는 멤버
//        else {
//            response = JwtUtil.createJwt(Opmember.get().getMemberId(), secretKey, expiredMs);
//        }
//
//        return response;
//    }
//
//    public KakaoProfile findKakaoProfile(String token) {
//
//        RestTemplate rt = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + token); //(1-4)
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
//
//        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoProfileRequest,
//                String.class
//        );
//
//        //System.out.println(kakaoProfileResponse);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        KakaoProfile kakaoProfile = new KakaoProfile();
//        try {
//            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
//        } catch (JsonProcessingException e) {
//            //System.out.println(kakaoProfile);
//            e.printStackTrace();
//        }
//
//        return kakaoProfile;
//    }
//}
