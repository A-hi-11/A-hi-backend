package com.example.Ahi.repository;

import com.example.Ahi.domain.ChatExample;
import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long>,
        JpaSpecificationExecutor<Prompt> {
    List<Prompt> findByMember(Member member);
}