package com.example.Ahi.repository;

import com.example.Ahi.domain.Text;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Text,Long> {
}
