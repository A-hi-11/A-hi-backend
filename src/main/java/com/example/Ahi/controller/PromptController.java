package com.example.Ahi.controller;

import com.example.Ahi.dto.PromptRequestDto;
import com.example.Ahi.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("prompt")
public class PromptController {
    @Autowired
    private final PromptService promptService;
    public PromptController(PromptService promptService){
        this.promptService = promptService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> createPrompt(@RequestBody PromptRequestDto prompt){
        return ResponseEntity.ok(promptService.create(prompt));
    }

}
