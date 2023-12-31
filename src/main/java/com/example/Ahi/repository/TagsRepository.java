package com.example.Ahi.repository;

import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    List<Tags> findByPrompt(Prompt prompt);
    void deleteByPrompt(Prompt prompt);
}
