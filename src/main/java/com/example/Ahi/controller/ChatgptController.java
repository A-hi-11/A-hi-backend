package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import com.example.Ahi.service.ChatgptService;
import com.example.Ahi.service.DiffusionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatgptController {

    @Autowired
    private final ChatgptService chatgptService;

    @PostMapping("/gpt/{chat_room_id}")
    public ChatgptResponse getGpt(Authentication authentication,
                                  @PathVariable("chat_room_id") Long chat_room_id,
                                  @RequestBody ChatgptRequest request){
        String memberId = authentication.getName();
        return chatgptService.getGpt(memberId,chat_room_id,request.getPrompt(), request.getGptConfigInfo());
    }

    @PostMapping("/gpt/use/{prompt_id}")
    public ResponseEntity useGpt(Authentication authentication,
                                 @PathVariable("prompt_id")Long prompt_id,
                                 @RequestBody ChatgptRequest request){

        String memberId = authentication.getName();
        ChatgptResponse response = chatgptService.useGpt(memberId,prompt_id,request);
        return ResponseEntity.ok().body(response);
    }
}
