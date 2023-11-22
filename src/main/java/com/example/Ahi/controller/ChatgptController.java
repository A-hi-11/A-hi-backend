package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import com.example.Ahi.service.ChatgptService;
import com.example.Ahi.service.DiffusionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatgptController {

    @Autowired
    private final ChatgptService chatgptService;

    @PostMapping("/gpt")
    public ChatgptResponse getGpt(@RequestBody String request){
        System.out.println(request);
        return chatgptService.getGpt(request);

    }

    @PostMapping("/gpt/use/{prompt_id}")
    public ResponseEntity useGpt(@PathVariable("prompt_id")Long prompt_id,
                                 @RequestBody ChatgptRequest request){

        ChatgptResponse response = chatgptService.useGpt(prompt_id,request);
        return ResponseEntity.ok().body(response);
    }
}
