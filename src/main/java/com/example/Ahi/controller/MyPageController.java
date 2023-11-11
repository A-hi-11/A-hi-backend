package com.example.Ahi.controller;

import com.example.Ahi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

}
