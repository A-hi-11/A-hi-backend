package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatExampleRepository extends JpaRepository<ChatExample, Long> {
    List<ChatExample> findByPrompt(Prompt prompt);
}
