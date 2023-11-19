package com.example.Ahi.config;

import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.requestDto.ChatgptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class ChatgptConfig {

    @Value("${gpt-key}")
    private String key;


    @Bean
    public HttpHeaders gptHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + key);

        return headers;
    }

    @Bean
    public ChatgptRequestDto gptBody(){
        ChatgptRequestDto requestDto = new ChatgptRequestDto();
        requestDto.setModel("gpt-3.5-turbo");


        return requestDto;
    }
}
