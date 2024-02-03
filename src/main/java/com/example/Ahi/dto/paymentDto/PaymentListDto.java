package com.example.Ahi.dto.paymentDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentListDto {
    private String orderId;
    private Long amount;
    private String paymentKey;
    private String memberId;
    private String status;
}
