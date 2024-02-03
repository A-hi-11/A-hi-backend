package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentListDto;
import com.example.Ahi.dto.responseDto.PromptListResponseDto;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentDetailService {
    private final PaymentUtils paymentUtils;
    public void getPaymentDetails(String orderId, String memberId) throws UnsupportedEncodingException {
        Payment payment = paymentUtils.getPayment(orderId);
        paymentUtils.verifyMember(payment.getMemberId().getMemberId(), memberId);
        fetchAndPrintPaymentInfo(payment);
    }
    public List<PaymentListDto> getPaymentList(String memberId){
        List<PaymentListDto> resultList = new ArrayList<>();
        for(Payment payment: paymentUtils.getPaymentList(memberId)){
            resultList.add(payment.toPaymentListDto());
        }
        return resultList;
    }
    private void fetchAndPrintPaymentInfo(Payment payment) throws UnsupportedEncodingException {
        String paymentKey = payment.getPaymentKey();
        String url = generateUrl(paymentKey);
        String auth = paymentUtils.generateAuthorization();

        HttpHeaders headers = createHeadersWithAuthorization(auth);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        printResponse(response);
    }

    private String generateUrl(String paymentKey) {
        return "https://api.tosspayments.com/v1/payments/" + paymentKey;
    }

    private HttpHeaders createHeadersWithAuthorization(String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", auth);
        return headers;
    }

    private void printResponse(ResponseEntity<String> response) {
        System.out.println("Response: " + response);
    }
}
