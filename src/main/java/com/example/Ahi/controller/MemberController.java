package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.MemberRequest;
import com.example.Ahi.dto.requestDto.SigninRequest;
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

    @PostMapping("/signin")
    public ResponseEntity login(@RequestBody SigninRequest request){
        String response = memberService.login(request.getUserId(), request.getUserPassword());
        return ResponseEntity.ok().body(response);
    }
}
