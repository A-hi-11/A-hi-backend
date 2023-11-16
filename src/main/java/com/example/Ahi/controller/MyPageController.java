package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.NicknameRequest;
import com.example.Ahi.dto.requestDto.PasswordRequest;
import com.example.Ahi.dto.requestDto.ProfileImgRequest;
import com.example.Ahi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordRequest new_password){
        return ResponseEntity.ok(myPageService.updatePassword(new_password.getNew_password()));
    }

    @PutMapping("/image")
    public ResponseEntity<String> updateProfileImg(@RequestBody ProfileImgRequest new_profileImg){
        return ResponseEntity.ok(myPageService.updateProfileImg(new_profileImg.getNew_profileImg()));
    }

    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestBody NicknameRequest new_nickname){
        return ResponseEntity.ok(myPageService.updateNickname(new_nickname.getNew_nickname()));
    }

}
