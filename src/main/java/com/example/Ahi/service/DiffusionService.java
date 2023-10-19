package com.example.Ahi.service;

// Service
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiffusionService {
    @Value("${diffusion-api-key}")
    private String diffusion_api_key;

    public ResponseEntity<byte[]> getDiffusion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + diffusion_api_key);

        // JSON 형식의 요청 본문 설정
        Map<String, String> body = new HashMap<>();
        body.put("inputs", prompt);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // POST 요청 보내기
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(response.getBody());
    }
}