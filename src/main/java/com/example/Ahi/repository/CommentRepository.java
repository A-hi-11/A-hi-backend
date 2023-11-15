package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Comment;
import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPromptId(Prompt promptId);
    void deleteByPromptId(Prompt promptId);

}
