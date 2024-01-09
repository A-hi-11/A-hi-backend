package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptListRequestDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.service.PromptService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("prompt")
public class PromptController {
    private final PromptCreationService promptCreationService;
    private final PromptRetrievalService promptRetrievalService;
    private final PromptUpdateService promptUpdateService;
    private final PromptDeletionService promptDeletionService;

    @PostMapping("/create")
    public ResponseEntity<String> createPrompt(Authentication authentication,
                                               @RequestBody PromptRequestDto prompt){
        String memberId = authentication.getName();
        prompt.setMember_id(memberId);
        return ResponseEntity.ok(promptCreationService.create(prompt));
    }

    @PostMapping("/view")
    public ResponseEntity<List<PromptListResponseDto>> getPromptList(
            @RequestBody PromptListRequestDto promptListRequestDto) {
        return ResponseEntity.ok(promptRetrievalService.getPromptList(promptListRequestDto));
    }

    @GetMapping("/view/info")
    public ResponseEntity<PromptResponseDto> getPrompt(@RequestParam("prompt_id") long prompt_id,
                                                       @RequestParam("member_id") String memberId){
        return ResponseEntity.ok(promptRetrievalService.getPrompt(prompt_id, memberId));
    }

    @PostMapping("/like")
    public ResponseEntity<String> addPreference(Authentication authentication,
                                                @RequestBody PreferenceRequestDto preferenceRequestDto){

        String memberId = authentication.getName();
        preferenceRequestDto.setMember_id(memberId);
        return ResponseEntity.ok(promptUpdateService.addPreference(preferenceRequestDto));
    }

    @GetMapping("/my-page")
    public ResponseEntity<ArrayList<PromptListResponseDto>> getMyPromptList(Authentication authentication){
        String memberId = authentication.getName();
        ArrayList<PromptListResponseDto> promptList = promptRetrievalService.getMyList(memberId);
        return ResponseEntity.ok(promptList);
    }

    @PutMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> modifyPrompt(Authentication authentication,
                                               @PathVariable Long prompt_id,
                                               @RequestBody PromptRequestDto promptRequestDto){

        String memberId = authentication.getName();
        promptRequestDto.setMember_id(memberId);
        return ResponseEntity.ok(promptUpdateService.modifyPrompt(prompt_id, promptRequestDto));

    }

    @DeleteMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> deletePrompt(Authentication authentication,
                                               @PathVariable Long prompt_id){

        String memberId = authentication.getName();
        return ResponseEntity.ok(promptDeletionService.deletePrompt(prompt_id, memberId));
    }

}
