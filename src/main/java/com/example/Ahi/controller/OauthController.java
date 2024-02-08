package com.example.Ahi.controller;

import com.example.Ahi.service.OAuthService.GoogleService;
import com.example.Ahi.service.OAuthService.NaverSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class OauthController {

    private final GoogleService googleService;
    private final NaverSerivce naverSerivce;


    @GetMapping( "/google-login")
    public String redirectToGoogle() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping( "/naver-login")
    public String redirectToNaver() {
        return "redirect:/oauth2/authorization/naver";
    }


    @GetMapping("/google/redirect")
    public ResponseEntity<?> googleOAuthLoginRedirect(@RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(googleService.getGoogleAccessToken(code)));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);

    }

    @GetMapping("/naver/redirect")
    public ResponseEntity<String> naverLogin(@RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(naverSerivce.naverLogin(code)));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

//    @GetMapping("/kakao/redirect")
//    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) {
//
//        String response = kakaoService.kakaoLogin(code);
////        HttpHeaders header = new HttpHeaders();
////        header.set("Authorization", response);
//        return ResponseEntity.ok("카카오 로그인+회원가입 성공 : " + response);
//    }
}
