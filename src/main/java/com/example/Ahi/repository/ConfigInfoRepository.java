package com.example.Ahi.repository;

import com.example.Ahi.domain.ConfigInfo;
import com.example.Ahi.domain.Prompt;
import com.example.Ahi.domain.Tags;
import com.example.Ahi.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConfigInfoRepository extends JpaRepository<ConfigInfo,Long> {
    @Query(value = "SELECT * from config_info WHERE prompt_id = :promptId",nativeQuery = true)
    Optional<ConfigInfo> findByPromptId(@Param("promptId")Long promptId);
    void deleteByPromptId(Prompt prompt);
}
