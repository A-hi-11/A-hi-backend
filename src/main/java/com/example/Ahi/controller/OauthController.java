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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;
    private final GoogleService googleService;


    @GetMapping("/kakao/redirect")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) {

        String response = oauthService.kakaoLogin(code);
//        HttpHeaders header = new HttpHeaders();
//        header.set("Authorization", response);
        return ResponseEntity.ok("카카오 로그인+회원가입 성공 : " + response);
    }
    @GetMapping( "/google-login")
    public String redirectToGoogle() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/naver/redirect")
    public ResponseEntity<String> naverLogin(@RequestParam("code") String code) {
        String response = oauthService.naverLogin(code);
        return ResponseEntity.ok("네이버 로그인+회원가입 성공 : " + response);
    }

    @GetMapping("/google/redirect")
    public ResponseEntity<?> googleOAuthLoginRedirect(@RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(googleService.getGoogleAccessToken(code)));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);

    }
}
