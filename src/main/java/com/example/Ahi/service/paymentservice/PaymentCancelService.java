package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentCancelDto;
import com.example.Ahi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentUtils paymentUtils;
    // TODO: 결제 취소 메소드 추가 필요
    public ResponseEntity<String> cancelPayment(PaymentCancelDto request, String memberId) throws UnsupportedEncodingException {
        Payment payment = paymentUtils.getPayment(request.getOrderId());
        paymentUtils.verifyMember(payment.getMemberId().getMemberId(), memberId);

        String url = generatePaymentUrl(payment.getPaymentKey());
        HttpEntity<String> entity = createHttpEntity(request.getCancelReason());
        // 성공 여부에 따라 다른 메세지 보내기
        return sendCancelRequest(url, entity);
    }


    private String generatePaymentUrl(String paymentKey) {
        return "https://api.tosspayments.com/v1/payments/" + paymentKey +"/cancel";
    }

    private HttpEntity<String> createHttpEntity(String cancelReason) throws UnsupportedEncodingException {
        String auth = paymentUtils.generateAuthorization();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", auth);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cancelReason", cancelReason);

        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private ResponseEntity<String> sendCancelRequest(String url, HttpEntity<String> entity) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("Response: " + response);

        return response;
    }
}
