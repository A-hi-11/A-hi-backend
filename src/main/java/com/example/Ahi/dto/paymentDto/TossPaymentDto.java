package com.example.Ahi.dto.paymentDto;

import lombok.Data;

@Data
public class TossPaymentDto {
    String version;
    String paymentKey;
    String type;
    String orderId;
    String orderName;
    String mid;

    String currency;
    String method;
    String totalAmount;
    String balanceAmount;
    String status;
    String requestedAt;
    String approvedAt;
    String useEscrow;
    String lastTransactionKey;
    String suppliedAmount;
    String vat;
    String cultureExpense;
    Long taxFreeAmount;
    TossPaymentCardDto card;

}
