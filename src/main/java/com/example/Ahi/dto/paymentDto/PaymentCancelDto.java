package com.example.Ahi.dto.paymentDto;

import lombok.Data;

@Data
public class PaymentCancelDto {
    String orderId;
    String cancelReason;
}
