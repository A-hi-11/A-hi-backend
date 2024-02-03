package com.example.Ahi.dto.paymentDto;

import com.example.Ahi.domain.Payment;
import lombok.Data;

@Data
public class PaymentConfirmRequestDto {
    String orderId;
    String PaymentKey;
    Long amount;

}
