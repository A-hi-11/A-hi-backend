package com.example.Ahi.repository;

import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromptRepository extends JpaRepository<Prompt, Long>,
        JpaSpecificationExecutor<Prompt> {
}