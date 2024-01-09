package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.NicknameRequest;
import com.example.Ahi.dto.requestDto.PasswordCheckRequest;
import com.example.Ahi.dto.requestDto.PasswordUpdateRequest;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @PutMapping("/password/update")
    public ResponseEntity<String> updatePassword(Authentication authentication,
                                                 @RequestBody PasswordUpdateRequest new_password){

        String memberId = authentication.getName();
        return ResponseEntity.ok(myPageService.updatePassword(memberId,new_password.getNew_password()));
    }

    @PutMapping("/password/check")
    public ResponseEntity<String> checkPassword(Authentication authentication,
                                                @RequestBody PasswordCheckRequest cur_password){
        String memberId = authentication.getName();
        return ResponseEntity.ok(myPageService.checkPassword(memberId,cur_password.getCur_password()));
    }

    @PutMapping("/image")
    public ResponseEntity<String> updateProfileImg(Authentication authentication,
                                                   @RequestPart("profileImage") MultipartFile imgFile){
        String memberId = authentication.getName();
        return ResponseEntity.ok(myPageService.updateProfileImg(memberId,imgFile));
    }

    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(Authentication authentication,
                                                 @RequestBody NicknameRequest new_nickname){
        String memberId = authentication.getName();
        return ResponseEntity.ok(myPageService.updateNickname(memberId, new_nickname.getNew_nickname()));
    }

    @GetMapping("/likes")
    public ResponseEntity<List<PromptListResponseDto>> getLikedPrompt(Authentication authentication) {
        String memberId = authentication.getName();
        ArrayList<PromptListResponseDto> likedPromptList = myPageService.getLikedPrompt(memberId);
        return ResponseEntity.ok(likedPromptList);
    }

}
