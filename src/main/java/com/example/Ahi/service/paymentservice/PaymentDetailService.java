package com.example.Ahi.service.paymentservice;

import com.example.Ahi.domain.Payment;
import com.example.Ahi.dto.paymentDto.PaymentListDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentDetailService {
    private final PaymentUtils paymentUtils;
    public ResponseEntity<JSONObject> getPaymentDetails(String orderId, String memberId) throws UnsupportedEncodingException, ParseException {
        Payment payment = paymentUtils.getPayment(orderId);
        paymentUtils.verifyMember(payment.getMemberId().getMemberId(), memberId);
        return fetchAndReturnPaymentInfo(payment);

    }
    public ResponseEntity<List<PaymentListDto>> getPaymentList(String memberId){
        List<PaymentListDto> resultList = new ArrayList<>();
        for(Payment payment: paymentUtils.getPaymentList(memberId)){
            resultList.add(payment.toPaymentListDto());

        }
        return ResponseEntity.ok(resultList);
    }
    private ResponseEntity<JSONObject> fetchAndReturnPaymentInfo(Payment payment) throws UnsupportedEncodingException, ParseException {
        String paymentKey = payment.getPaymentKey();
        String url = generateUrl(paymentKey);
        String auth = paymentUtils.generateAuthorization();

        HttpHeaders headers = createHeadersWithAuthorization(auth);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String utf8Body = new String(response.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(utf8Body);
        JSONObject jsonObj = (JSONObject) obj;
        return ResponseEntity.ok(jsonObj);
    }

    private String generateUrl(String paymentKey) {
        return "https://api.tosspayments.com/v1/payments/" + paymentKey;
    }

    private HttpHeaders createHeadersWithAuthorization(String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", auth);
        return headers;
    }
}
