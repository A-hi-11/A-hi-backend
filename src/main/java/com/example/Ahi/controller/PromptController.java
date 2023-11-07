package com.example.Ahi.controller;

import com.example.Ahi.dto.responseDto.PromptRequestDto;
import com.example.Ahi.dto.requestDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/view/{id}")
    public ResponseEntity<PromptResponseDto> getPrompt(@PathVariable Long id){
        return ResponseEntity.ok(promptService.getPrompt(id));
    }
}
