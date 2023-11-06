package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import com.example.Ahi.service.ChatgptService;
import com.example.Ahi.service.DiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatgptController {

    @Autowired
    private ChatgptService chatgptService;

    @PostMapping("/gpt")
    public ChatgptResponse getGpt(@RequestBody ChatgptRequest request){
        return chatgptService.getGpt(request);

    }
}
