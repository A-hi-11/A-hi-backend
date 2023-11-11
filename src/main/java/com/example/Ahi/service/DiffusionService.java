package com.example.Ahi.service;

// Service

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiffusionService {
    @Value("${diffusion-api-key}")
    private String key;

    private final S3Service s3Service;
    public String getDiffusion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + key);

        // JSON 형식의 요청 본문 설정
        String argument = ",realistic,best,4k";
        Map<String, String> body = new HashMap<>();
        body.put("inputs", prompt + argument);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // POST 요청 보내기
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);

        // TODO: chatroom 생성 시 imgUrl을 함께 저장해줘야 함.
        return s3Service.uploadDiffusionImage(response.getBody());
    }
}