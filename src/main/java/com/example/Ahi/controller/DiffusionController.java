package com.example.Ahi.controller;


// Controller
import com.example.Ahi.dto.requestDto.ModelRequestDto;
import com.example.Ahi.dto.responseDto.ModelResponseDto;
import com.example.Ahi.service.DiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiffusionController {

    @Autowired
    private DiffusionService diffusionService;

    @PostMapping("/diffusion")
    public ResponseEntity<ModelResponseDto> getDiffusion(@RequestBody ModelRequestDto prompt) {
        return ResponseEntity.ok(diffusionService.getDiffusion(prompt));
    }

    @PostMapping("/diffusion/{prompt_id}")
    public ResponseEntity<ModelResponseDto> getDiffusionByPrompt(@PathVariable long prompt_id,
                                                                   @RequestBody ModelRequestDto prompt) {
        return ResponseEntity.ok(diffusionService.getDiffusionByPrompt(prompt_id, prompt));
    }
}