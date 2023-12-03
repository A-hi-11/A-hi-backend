package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.PreferenceRequestDto;
import com.example.Ahi.dto.requestDto.PromptRequestDto;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.dto.responseDto.PromptResponseDto;
import com.example.Ahi.service.PromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("prompt")
public class PromptController {
    private final PromptService promptService;
    public PromptController(PromptService promptService){
        this.promptService = promptService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> createPrompt(Authentication authentication,
                                               @RequestBody PromptRequestDto prompt){
        String memberId = authentication.getName();
        return ResponseEntity.ok(promptService.create(prompt));
    }

    @GetMapping("/view")
    public ResponseEntity<List<PromptListResponseDto>> getPromptList(
            Authentication authentication,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        String memberId = authentication.getName();
        List<PromptListResponseDto> promptList = promptService.getPromptList(sort, category, search);
        return ResponseEntity.ok(promptList);
    }

    @GetMapping("/view/info")
    public ResponseEntity<PromptResponseDto> getPrompt(Authentication authentication,
                                                       @RequestParam("prompt_id") long prompt_id,
                                                       @RequestParam("member_id") String member_id){

        String memberId = authentication.getName();
        return ResponseEntity.ok(promptService.getPrompt(prompt_id, member_id));
    }

    @PostMapping("/like")
    public ResponseEntity<String> addPreference(Authentication authentication,
                                                @RequestBody PreferenceRequestDto preferenceRequestDto){

        String memberId = authentication.getName();
        return ResponseEntity.ok(promptService.addPreference(preferenceRequestDto));
    }

    @GetMapping("/my-page/{member_id}")
    public ResponseEntity<ArrayList<PromptListResponseDto>> getMyPromptList(Authentication authentication,
                                                                            @PathVariable String member_id){

        String memberId = authentication.getName();
        ArrayList<PromptListResponseDto> promptList = promptService.getMyList(member_id);
        return ResponseEntity.ok(promptList);
    }

    @PutMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> modifyPrompt(Authentication authentication,
                                               @PathVariable Long prompt_id,
                                               @RequestBody PromptRequestDto promptRequestDto){

        String memberId = authentication.getName();
        return ResponseEntity.ok(promptService.modifyPrompt(prompt_id, promptRequestDto));

    }

    @DeleteMapping("/my-page/{prompt_id}")
    public ResponseEntity<String> deletePrompt(Authentication authentication,
                                               @PathVariable Long prompt_id){

        String memberId = authentication.getName();
        return ResponseEntity.ok(promptService.deletePrompt(prompt_id));
    }

}
