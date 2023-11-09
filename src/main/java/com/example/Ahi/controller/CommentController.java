package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.CommentRequest;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.CommentResponse;
import com.example.Ahi.service.CommentService;
import com.example.Ahi.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("prompt/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{prompt_id}")
    public ResponseEntity create_comment(@PathVariable("prompt_id")Long id,
                                         @RequestBody CommentRequest request){

        CommentResponse response = commentService.create_comment(id, request.getComment());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{comment_id}")
    public ResponseEntity delete_comment(@PathVariable("comment_id")Long id){


        CommentResponse response = commentService.delete_comment(id);
        return ResponseEntity.ok().body(response);
    }





}
