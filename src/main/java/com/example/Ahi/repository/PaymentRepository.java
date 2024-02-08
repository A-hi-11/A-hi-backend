package com.example.Ahi.repository;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import com.example.Ahi.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByMemberId(Member member);
}
