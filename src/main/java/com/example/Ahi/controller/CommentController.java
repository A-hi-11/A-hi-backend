package com.example.Ahi.controller;

import com.example.Ahi.dto.requestDto.CommentRequest;
import com.example.Ahi.dto.responseDto.CommentListResponse;
import com.example.Ahi.dto.responseDto.CommentResponse;
import com.example.Ahi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("prompt/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{prompt_id}")
    public ResponseEntity<CommentResponse> create_comment(Authentication authentication,
                                         @PathVariable("prompt_id")Long id,
                                         @RequestBody CommentRequest request){

        String memberId = authentication.getName();
        CommentResponse response = commentService.create_comment(memberId,id,request.getComment());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/delete/{comment_id}")
    public ResponseEntity<CommentResponse> delete_comment(Authentication authentication,
                                         @PathVariable("comment_id")Long id){

        String memberId = authentication.getName();
        CommentResponse response = commentService.delete_comment(memberId,id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/read/{prompt_id}")
    public ResponseEntity<List<CommentListResponse>> read_comment(Authentication authentication,
                                       @PathVariable("prompt_id")Long prompt_id){

        String memberId = authentication.getName();
        List<CommentListResponse> response = commentService.read_comment(prompt_id,memberId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{comment_id}")
    public ResponseEntity<CommentResponse> update_comment(Authentication authentication,
                                         @PathVariable("comment_id")Long id,
                                         @RequestBody CommentRequest request){

        String memberId = authentication.getName();
        CommentResponse response = commentService.update_comment(memberId,id,request.getComment());
        return ResponseEntity.ok().body(response);
    }





}
