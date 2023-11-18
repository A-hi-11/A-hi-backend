package com.example.Ahi.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("memberCheck")
public class TempController {
    @GetMapping()
    public ResponseEntity memberResult(Authentication authentication){
        String response = "";
        if(authentication == null){
            response="존재하지 않는 사용자";
        }
        else{
            response=authentication.getName()+"님 안녕하세요";
        }

        return ResponseEntity.ok().body(response);
    }
}
