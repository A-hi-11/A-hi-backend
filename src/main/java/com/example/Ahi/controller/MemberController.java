package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.MemberRequest;
import com.example.Ahi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    @PostMapping("/signup") //회원가입
    public ResponseEntity signup(@RequestBody MemberRequest request){

        String response = memberService.signup(request);

        return ResponseEntity.ok().body(response);

    }
}
