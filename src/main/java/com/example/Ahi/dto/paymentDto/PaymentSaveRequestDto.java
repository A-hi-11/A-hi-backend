package com.example.Ahi.dto.paymentDto;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import lombok.Data;

@Data
public class PaymentSaveRequestDto {
    String orderId;
    Long amount;

    public Payment toPayment(Member memberId){
        return Payment.builder()
                .paymentKey(null)
                .status("대기")
                .amount(amount)
                .orderId(orderId)
                .memberId(memberId)
                .build();

    }
}
