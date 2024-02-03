package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentConfirmRequestDto;
import com.example.Ahi.repository.PaymentRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class PaymentConfirmServiceTest {
    @InjectMocks
    private PaymentConfirmService paymentConfirmService;

    @Mock
    private PaymentUtils paymentUtils;

    @Value("${toss-secret-key}")
    private String secretKey;
    @Mock
    private PaymentRepository paymentRepository;

    private PaymentConfirmRequestDto requestDto;
    private Payment payment;
    private String auth;
    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        requestDto = new PaymentConfirmRequestDto();
        requestDto.setOrderId("testOrderId");
        requestDto.setAmount(100L);
        requestDto.setPaymentKey("testPaymentKey");

        payment = Payment.builder()
                .amount(100L)
                .orderId("testOrderId")
                .paymentKey("testPaymentKey")
                .memberId(new Member())
                .status("대기")
                .build();

         auth = generateAuthorization();
    }
    private String generateAuthorization() throws UnsupportedEncodingException {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes("UTF-8"));
        return "Basic " + new String(encodedBytes);
    }
    @Test
    public void confirmPaymentTest() throws Exception {
        when(paymentUtils.getPayment(requestDto.getOrderId())).thenReturn(payment);
        when(paymentUtils.generateAuthorization()).thenReturn(auth);

        ResponseEntity<JSONObject> response = paymentConfirmService.confirmPayment(requestDto, "testMemberId");
        System.out.println(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}
