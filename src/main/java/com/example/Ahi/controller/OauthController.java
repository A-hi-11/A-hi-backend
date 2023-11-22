package com.example.Ahi.controller;

import com.example.Ahi.service.GoogleService;
import com.example.Ahi.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;
    private final GoogleService googleService;


    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("Access-Code") String code) {

        String response = oauthService.kakaoLogin(code);
//        HttpHeaders header = new HttpHeaders();
//        header.set("Authorization", response);
        return ResponseEntity.ok("카카오 로그인+회원가입 성공 : " + response);
    }


    @GetMapping("/naver/redirect")
    public ResponseEntity<String> naverLogin(@RequestParam("code") String code) {
        String response = oauthService.naverLogin(code);
        return ResponseEntity.ok("네이버 로그인+회원가입 성공 : " + response);
    }


    @GetMapping("/google/redirect")
    public ResponseEntity<String> googleOAuthLoginRedirect(@RequestParam("code") String code) {
        String accessToken = googleService.getGoogleAccessToken(code);
        return ResponseEntity.ok(accessToken);
    }

}
