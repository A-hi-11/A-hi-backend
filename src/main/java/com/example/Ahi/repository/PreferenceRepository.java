package com.example.Ahi.repository;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Preference;
import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenceRepository extends JpaRepository<Preference,Long> {
    List<Preference> findByMemberAndPrompt(Member member, Prompt prompt);

    List<Preference> findByPrompt(Prompt prompt);
    void deleteByPrompt(Prompt prompt);

    List<Preference> findByMemberAndStatus(Member member, String status);
}
