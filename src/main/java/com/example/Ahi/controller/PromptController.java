package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptInfoDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.service.PromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("prompt")
public class PromptController {
    private final PromptService promptService;
    public PromptController(PromptService promptService){
        this.promptService = promptService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> createPrompt(@RequestBody PromptRequestDto prompt){
        return ResponseEntity.ok(promptService.create(prompt));
    }

    @GetMapping("/view")
    public ResponseEntity<ArrayList<PromptListResponseDto>> getPromptList(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search) {
        ArrayList<PromptListResponseDto> promptList = promptService.getPromptList(sort, search);
        return ResponseEntity.ok(promptList);
    }

    @GetMapping("/view/info")
    public ResponseEntity<PromptResponseDto> getPrompt(@RequestBody PromptInfoDto promptInfoDto){
        return ResponseEntity.ok(promptService.getPrompt(promptInfoDto));
    }

    @PostMapping("/like")
    public ResponseEntity<String> addPreference(@RequestBody PreferenceRequestDto preferenceRequestDto){
        return ResponseEntity.ok(promptService.addPreference(preferenceRequestDto));
    }

    @GetMapping("/my-page/{member_id}")
    public ResponseEntity<ArrayList<PromptListResponseDto>> getMyPromptList(@PathVariable String member_id){
        ArrayList<PromptListResponseDto> promptList = promptService.getMyList(member_id);
        return ResponseEntity.ok(promptList);
    }

    @PutMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> modifyPrompt(@PathVariable Long prompt_id,
                                               @RequestBody PromptRequestDto promptRequestDto){
        return ResponseEntity.ok(promptService.modifyPrompt(prompt_id, promptRequestDto));

    }

    @DeleteMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> deletePrompt(@PathVariable Long prompt_id){
        return ResponseEntity.ok(promptService.deletePrompt(prompt_id));
    }

}
