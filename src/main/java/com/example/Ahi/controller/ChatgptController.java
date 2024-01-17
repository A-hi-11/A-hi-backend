package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.ChatgptRequest;
import com.example.Ahi.dto.responseDto.ChatgptResponse;
import com.example.Ahi.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class ChatgptController {
    private final ChatgptService chatgptService;

    @PostMapping(value = "/gpt/{chat_room_id}" ,produces = "text/event-stream")
    public ResponseEntity<SseEmitter> getGpt(Authentication authentication,
                                             @PathVariable("chat_room_id") Long chat_room_id,
                                             @RequestBody ChatgptRequest request){

        String memberId = authentication.getName();
        SseEmitter response = chatgptService.getGpt(
                memberId,
                chat_room_id,
                request.getPrompt(),
                request.getGptConfigInfo());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/gpt/use/{prompt_id}" ,produces = "text/event-stream")
    public ResponseEntity<SseEmitter> useGptwithPrompt(Authentication authentication,
                                 @PathVariable("prompt_id")Long prompt_id,
                                 @RequestBody ChatgptRequest request){

        String memberId = authentication.getName();
        SseEmitter response = chatgptService.useGptwithPrompt(memberId,prompt_id,request);
        return ResponseEntity.ok().body(response);
    }
}
