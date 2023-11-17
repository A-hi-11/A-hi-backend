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
        return ResponseEntity.ok().body(authentication.getName()+"님 안녕하세요");
    }
}
