package com.example.Ahi.service;

import com.example.Ahi.dto.requestDto.ChatgptRequestDto;
import com.example.Ahi.dto.requestDto.Message;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatgptService {
    @Value("${gpt-key}")
    private String key;


    public ChatgptResponse getGpt(ChatgptRequest request){
        String url = "https://api.openai.com/v1/chat/completions";
        ChatgptResponse response = new ChatgptResponse();

        //요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + key);

        //요청 바디
        ChatgptRequestDto requestDto = new ChatgptRequestDto();
        requestDto.setModel("gpt-3.5-turbo");
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", request.getPrompt()));
        requestDto.setMessages(messages);
        requestDto.setMaxTokens(100);
        requestDto.setTemperature(1.0);
        requestDto.setTopP(1.0);

        HttpEntity<ChatgptRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String,Object>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,new ParameterizedTypeReference<Map<String,Object>>() {});

        //response 파싱
        Map<String,Object> result = responseEntity.getBody();
        assert result != null; //여기 예외처리 필요
        @SuppressWarnings("unchecked")
        ArrayList<Map<String,Object>> jsonArray = (ArrayList<Map<String,Object>>)result.get("choices");
        @SuppressWarnings("unchecked")
        Map<Object,Object> jsonObject = (Map<Object,Object>)jsonArray.get(0).get("message");


        response.setAnswer(String.valueOf(jsonObject.get("content")));
        return response;
    }

}
