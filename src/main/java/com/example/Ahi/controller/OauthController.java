package com.example.Ahi.controller;

import com.example.Ahi.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @GetMapping("/kakao")
    public ResponseEntity<String> oauthLogin(@RequestHeader("Access-Code") String code) {

        String accessToken = oauthService.getAccessToken(code).getAccess_token();

//        HttpHeaders header = new HttpHeaders();
//        header.set("Authorization", accessToken);

        return ResponseEntity.ok(accessToken);
    }

}
