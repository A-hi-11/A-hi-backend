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

    @GetMapping("auth/kakao")
    public ResponseEntity<String> oauthLogin(@RequestHeader("Access-Code") String code) {

        String accessToken = oauthService.getAccessToken(code).getRefresh_token();


        return ResponseEntity.ok(accessToken);
    }

//    @PostMapping("/google")
//    public ResponseEntity<String> googleOAuthLogin() {
//
//
//        return ResponseEntity.ok();
//    }

    @GetMapping("/google/redirect")
    public ResponseEntity<String> googleOAuthLoginRedirect(@RequestParam("code") String code) {
        String accessToken = googleService.getGoogleAccessToken(code);
        return ResponseEntity.ok(accessToken);
    }

}
