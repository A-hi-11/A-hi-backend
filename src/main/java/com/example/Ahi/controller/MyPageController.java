package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.NicknameRequest;
import com.example.Ahi.dto.requestDto.PasswordCheckRequest;
import com.example.Ahi.dto.requestDto.PasswordUpdateRequest;
import com.example.Ahi.dto.requestDto.ProfileImgRequest;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest new_password){
        return ResponseEntity.ok(myPageService.updatePassword(new_password.getNew_password()));
    }

    @PutMapping("/password/check")
    public ResponseEntity<String> checkPassword(@RequestBody PasswordCheckRequest cur_password){
        return ResponseEntity.ok(myPageService.checkPassword(cur_password.getCur_password()));
    }

    @PutMapping("/image")
    public ResponseEntity<String> updateProfileImg(@RequestPart("profileImage") MultipartFile imgFile){
        return ResponseEntity.ok(myPageService.updateProfileImg(imgFile));
    }

    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestBody NicknameRequest new_nickname){
        return ResponseEntity.ok(myPageService.updateNickname(new_nickname.getNew_nickname()));
    }

    @GetMapping("/likes")
    public ResponseEntity<List<PromptListResponseDto>> getLikedPrompt() {
        ArrayList<PromptListResponseDto> likedPromptList = myPageService.getLikedPrompt();
        return ResponseEntity.ok(likedPromptList);
    }

}
