package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Member;
import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PaymentDetailServiceTest {

    @InjectMocks
    private PaymentDetailService paymentDetailService;

    @Mock
    private PaymentUtils paymentUtils;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Payment payment;

    @Mock
    private PaymentListDto paymentListDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPaymentDetails() throws UnsupportedEncodingException {
        when(paymentUtils.getPayment(anyString())).thenReturn(payment);
        when(payment.getMemberId()).thenReturn(new Member());

        paymentDetailService.getPaymentDetails("testOrderId", "testMemberId");

        verify(paymentUtils, times(1)).getPayment(eq("testOrderId"));
        verify(paymentUtils, times(1)).verifyMember(any(), eq("testMemberId"));
    }

    @Test
    public void testGetPaymentList() {
        when(paymentUtils.getPaymentList(anyString())).thenReturn(Arrays.asList(payment));
        when(payment.toPaymentListDto()).thenReturn(paymentListDto);

        List<PaymentListDto> result = paymentDetailService.getPaymentList("testMemberId");

        assertEquals(1, result.size());
        verify(paymentUtils, times(1)).getPaymentList(eq("testMemberId"));
    }
}
