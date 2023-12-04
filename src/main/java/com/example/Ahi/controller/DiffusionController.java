package com.example.Ahi.controller;


// Controller
import com.example.Ahi.dto.requestDto.ModelRequestDto;
import com.example.Ahi.dto.responseDto.ModelResponseDto;
import com.example.Ahi.service.DiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiffusionController {

    @Autowired
    private DiffusionService diffusionService;

    @PostMapping("/diffusion")
    public ResponseEntity<ModelResponseDto> getDiffusion(Authentication authentication,
                                                         @RequestBody ModelRequestDto modelRequestDto) {
        String memberId = authentication.getName();
        modelRequestDto.setMember_id(memberId);
        return ResponseEntity.ok(diffusionService.getDiffusion(modelRequestDto));
    }

    @PostMapping("/diffusion/{prompt_id}")
    public ResponseEntity<ModelResponseDto> getDiffusionByPrompt(Authentication authentication,
                                                                 @PathVariable long prompt_id,
                                                                   @RequestBody ModelRequestDto modelRequestDto) {
        String memberId = authentication.getName();
        modelRequestDto.setMember_id(memberId);
        return ResponseEntity.ok(diffusionService.getDiffusionByPrompt(prompt_id, modelRequestDto));
    }
}