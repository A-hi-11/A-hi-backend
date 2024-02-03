package com.example.Ahi.domain;

import com.example.Ahi.dto.paymentDto.PaymentListDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Payment {
    @Id
    private String orderId;
    private Long amount;
    private String paymentKey;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member memberId;
    private String status;

    public PaymentListDto toPaymentListDto(){
        return PaymentListDto.builder()
                .amount(amount)
                .memberId(memberId.getMemberId())
                .orderId(orderId)
                .paymentKey(paymentKey)
                .status(status)
                .build();
    }
}
