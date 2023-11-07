package com.example.Ahi.service;

import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.dto.responseDto.CommentResponse;
import com.example.Ahi.repository.CommentRepository;
import com.example.Ahi.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PromptRepository promptRepository;
    public CommentResponse create_comment(Long prompt_id, String context){
        Comment comment = new Comment();
        Optional<Prompt> prompt = promptRepository.findById(prompt_id);
        CommentResponse response = new CommentResponse();

        if(prompt.isPresent()){
            comment.setCreate_time(LocalDateTime.now());
            comment.setContent(context);
            comment.setPrompt_id(prompt.get());
            // TODO : member 수정필요
            comment.setMember_id(new Member());
            commentRepository.save(comment);
            response.setMessage("성공적으로 저장하였습니다.");
        }
        else{
            response.setMessage("없는 프롬프트입니다. 저장에 실패했습니다.");
        }
        return response;
    }
}
