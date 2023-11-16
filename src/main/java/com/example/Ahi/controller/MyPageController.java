package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.PasswordRequest;
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

}
