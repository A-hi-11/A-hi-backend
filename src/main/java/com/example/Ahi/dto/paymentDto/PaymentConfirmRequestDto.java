package com.example.Ahi.dto.paymentDto;

import com.example.Ahi.domain.Payment;
import lombok.Data;

@Data
public class PaymentConfirmRequestDto {
    private String orderId;
    private String paymentKey;
    private Long amount;

}
