package com.example.Ahi.controller;


import com.example.Ahi.dto.requestDto.EmailDto;
import com.example.Ahi.dto.requestDto.MailCheckRequest;
import com.example.Ahi.dto.requestDto.MemberRequest;
import com.example.Ahi.dto.requestDto.SigninRequest;
import com.example.Ahi.dto.responseDto.LoginResponse;
import com.example.Ahi.service.MailService;
import com.example.Ahi.service.MemberService;
import com.example.Ahi.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MailService mailService;
    @Autowired
    public MailUtil mailUtil;


    @PostMapping("/signup") //회원가입
    public ResponseEntity<String> signup(@RequestBody MemberRequest request){

        String response = memberService.signup(request);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody SigninRequest request){

        LoginResponse response = memberService.loginAndReturnToken(request.getUserId(), request.getUserPassword());
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/mail")
    public ResponseEntity<Integer> emailConfirm(@RequestBody EmailDto email) throws Exception {

        int sendCode = mailUtil.sendMessage(email.getEmail());
        return ResponseEntity.ok().body(sendCode);
    }
    @PostMapping("/mail/check")
    public ResponseEntity<Boolean> codeCheck(@RequestBody MailCheckRequest request){

        boolean result = mailService.codeCheck(request.getEmail(),request.getCode());
        return ResponseEntity.ok().body(result);
    }
}
